package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment


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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.Priority
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.databinding.FragmentTaskBinding
import com.advancedsolutionsdevelopers.todoapp.utils.Constant
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.device_id_key
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.task_id_key
import com.advancedsolutionsdevelopers.todoapp.utils.Converters
import com.advancedsolutionsdevelopers.todoapp.utils.getThemeAttrColor
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID


class TaskFragment : Fragment() {
    private var taskId: String = ""
    private var isCalendarWasOpen = false
    private var menu: PriorityMenu? = null
    private var isEditMode = false
    private var firstLaunch: Boolean = true
    private var deadlineDate = LocalDate.of(2000, 1, 1)
    private var datePickerDialog: DatePickerDialog? = null
    private var task: TodoItem? = null
    private val viewModel: TaskViewModel by activityViewModels()
    private val converter: Converters = Converters()
    private val curDate = converter.dateToTimestamp(LocalDate.now())
    private lateinit var sp: SharedPreferences
    private var _binding: FragmentTaskBinding? = null
    private val binding: FragmentTaskBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = requireContext().getSharedPreferences(Constant.sp_name, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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
        taskId = requireArguments().getString(task_id_key, "-1")
        lifecycleScope.launch {
            viewModel.getItem(taskId).collect {
                isEditMode = it != null
                if (isEditMode) {
                    task = it
                }else if(!firstLaunch){
                    Toast.makeText(requireContext(), R.string.deleted, Toast.LENGTH_SHORT).show()
                    binding.closeButton.callOnClick()
                }
                firstLaunch=false
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

    private fun generateCalendarDialog(): DatePickerDialog {
        val datePickerListener = OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            binding.calendarButton.text = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            deadlineDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
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
        val x = if (!isEditMode) {
            TodoItem(
                UUID.randomUUID().toString(),
                binding.taskDescriptionEdittext.text.toString(),
                Priority.values()[menu!!.currentPriority],
                false,
                curDate,
                curDate,
                sp.getString(device_id_key, "_")!!,
                false,
                if (binding.doUntilSwitch.isChecked)
                    (if (isCalendarWasOpen) converter.dateToTimestamp(deadlineDate) else curDate) else null
            )
        } else {
            TodoItem(
                task!!.id,
                binding.taskDescriptionEdittext.text.toString(),
                if(menu!!.wasUsed) Priority.values()[menu!!.currentPriority] else task!!.priority,
                task!!.isCompleted,
                task!!.creationDate,
                curDate,
                sp.getString(device_id_key, "_")!!,
                false,
                if (binding.doUntilSwitch.isChecked) (if (isCalendarWasOpen) converter.dateToTimestamp(
                    deadlineDate
                ) else (if (task!!.deadlineDate != null) task!!.deadlineDate else curDate)) else null
            )
        }
        viewModel.saveItem(x, isEditMode)
    }

    private fun setupDeleteButton() {
        binding.deleteButton.isClickable = isEditMode
        if (isEditMode) {
            binding.deleteButton.setOnClickListener {
                viewModel.deleteItem(task!!)
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