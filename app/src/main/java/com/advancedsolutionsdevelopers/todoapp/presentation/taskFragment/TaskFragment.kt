package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.models.Priority
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import com.advancedsolutionsdevelopers.todoapp.databinding.FragmentTaskBinding
import com.advancedsolutionsdevelopers.todoapp.presentation.MainActivity
import com.advancedsolutionsdevelopers.todoapp.utils.Constant
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.DEVICE_ID_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TASK_ID_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.DeadlineIdentifier
import com.advancedsolutionsdevelopers.todoapp.utils.TimeFormatConverters
import com.advancedsolutionsdevelopers.todoapp.utils.getThemeAttrColor
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject


class TaskFragment : Fragment() {
    private var taskId: String = ""
    private var isCalendarWasOpen = false
    private var menu: PriorityMenu? = null
    private var isEditMode = false
    private var firstLaunch: Boolean = true
    private var deadlineDate = LocalDateTime.of(2000, 1, 1, 1, 1)
    private var datePickerDialog: DatePickerDialog? = null
    private var task: TodoItem? = null

    @Inject
    lateinit var modelFactory: TaskViewModelFactory
    private val model: TaskViewModel by lazy {
        ViewModelProvider(this, modelFactory)[TaskViewModel::class.java]
    }
    private val converter: TimeFormatConverters = TimeFormatConverters()
    private val curDate = converter.dateToTimestamp(LocalDateTime.now())
    private val deadlineIdentifier = DeadlineIdentifier()
    private lateinit var sp: SharedPreferences
    private var _binding: FragmentTaskBinding? = null
    private val binding: FragmentTaskBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = requireContext().getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent.inject(this)
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menu = PriorityMenu(
            ContextThemeWrapper(requireContext(), R.style.Priority_PopupMenu), binding
        )
        setupListeners()
        datePickerDialog = generateCalendarDialog()
        createFillViewsJob()
        setupSaveButton()
    }

    private fun createFillViewsJob() {
        taskId = requireArguments().getString(TASK_ID_KEY, "-1")
        lifecycleScope.launch {
            model.getItem(taskId).collect {
                isEditMode = it != null
                if (isEditMode) {
                    task = it
                    firstLaunch = false
                } else if (!firstLaunch) {
                    Toast.makeText(requireContext(), R.string.deleted, Toast.LENGTH_SHORT).show()
                    binding.closeButton.callOnClick()
                }
                fillViewsOnMode()
                setupDeleteButton()
            }
        }
    }

    private fun fillViewsOnMode() {
        if (isEditMode) {
            binding.taskDescriptionEdittext.setText(task!!.text)
            binding.priorityTextview.text = menu!!.menu.getItem(task!!.priority.ordinal).toString()
            binding.doUntilSwitch.isChecked = task!!.deadlineDate != null
            binding.calendarButton.text =
                converter.fromTimeStampToDate(if (task!!.deadlineDate != null) task!!.deadlineDate!! else curDate)
                    .toString()
        } else {
            binding.priorityTextview.text = menu!!.curPriorText
            binding.calendarButton.text = converter.fromTimeStampToDate(curDate).toString()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun generateCalendarDialog(): DatePickerDialog {
        val now = LocalDateTime.now()
        val datePickerListener = OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            binding.calendarButton.text = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            deadlineDate = LocalDateTime.of(
                selectedYear, selectedMonth + 1, selectedDay, now.hour, now.minute, now.second
            )
            isCalendarWasOpen = true
        }
        val cur = LocalDate.now(converter.zoneId)
        return DatePickerDialog(
            requireContext(), datePickerListener, cur.year, cur.monthValue - 1, cur.dayOfMonth
        )
    }

    private fun setupListeners() {
        binding.taskScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            binding.taskAppBar.elevation = if (scrollY > 0) 10f else 0f
        }
        binding.doUntilSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.calendarButton.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            binding.calendarButton.isClickable = isChecked
        }
        binding.closeButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.saving), Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.calendarButton.setOnClickListener {
            datePickerDialog?.show()
        }
        binding.priorityPopupMenuButton.setOnClickListener {
            menu?.show()
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (binding.taskDescriptionEdittext.text.toString() == "") {
                Toast.makeText(requireContext(), R.string.enter_something_first, Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            buildAndPostNewTask()
            binding.closeButton.callOnClick()
        }
    }

    private fun buildAndPostNewTask() {
        val x = if (isEditMode) {
            generateEditedItem()
        } else {
            generateNewItem()
        }
        model.saveItem(x, isEditMode)
    }

    private fun generateEditedItem(): TodoItem {
        return TodoItem(
            task!!.id,
            binding.taskDescriptionEdittext.text.toString(),
            if (menu!!.wasUsed) Priority.values()[menu!!.currentPriority] else task!!.priority,
            task!!.isCompleted,
            task!!.creationDate,
            curDate,
            sp.getString(DEVICE_ID_KEY, "_")!!,
            false,
            deadlineIdentifier.identify(
                false,
                isCalendarWasOpen,
                binding.doUntilSwitch.isChecked,
                deadlineDate,
                task
            )
        )
    }

    private fun generateNewItem(): TodoItem {
        return TodoItem(
            UUID.randomUUID().toString(),
            binding.taskDescriptionEdittext.text.toString(),
            Priority.values()[menu!!.currentPriority],
            false,
            curDate,
            curDate,
            sp.getString(DEVICE_ID_KEY, "_")!!,
            false,
            deadlineIdentifier.identify(
                true,
                isCalendarWasOpen,
                binding.doUntilSwitch.isChecked,
                deadlineDate
            )
        )
    }

    private fun setupDeleteButton() {
        binding.deleteButton.isClickable = isEditMode
        if (isEditMode) {
            binding.deleteButton.setOnClickListener {
                model.deleteItem(task!!)
            }
        }
        val color = getThemeAttrColor(
            requireContext(),
            if (isEditMode) R.attr.color_delete_active else R.attr.color_delete_inactive
        )
        binding.deleteTextview.setTextColor(color)
        binding.deleteImageview.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menu = null
        _binding = null
    }
}