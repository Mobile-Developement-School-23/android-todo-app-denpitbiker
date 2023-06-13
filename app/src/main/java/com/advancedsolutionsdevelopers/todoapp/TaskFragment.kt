package com.advancedsolutionsdevelopers.todoapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.advancedsolutionsdevelopers.todoapp.recyclerView.TodoItem
import com.google.android.material.appbar.AppBarLayout
import java.time.LocalDate
import java.util.ArrayList


class TaskFragment : Fragment() {
    private lateinit var activityContext: Context
    private var taskId:Long =-1
    private var taskIndex:Int=-1
    lateinit var viewModel: TasksListViewModel
    lateinit var data:ArrayList<TodoItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[TasksListViewModel::class.java]
        data= viewModel.tasks.value!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityContext = requireContext()
        val curDate = LocalDate.now()
        var deadlineYear = 0
        var deadlineMonth = 0
        var deadlineDay = 0
        val priorities = resources.getStringArray(R.array.priority)
        val closeButton: ImageButton = view.findViewById(R.id.close_button)
        val saveButton: TextView = view.findViewById(R.id.save_button)
        val deleteButton: LinearLayout = view.findViewById(R.id.delete_button)
        val taskEditText: EditText = view.findViewById(R.id.task_description_edittext)
        val prioritySpinner: Spinner = view.findViewById(R.id.priority_spinner)
        val doUntilSwitch: Switch = view.findViewById(R.id.do_until_switch)
        val doUntilButton: TextView = view.findViewById(R.id.calendar_button)
        val taskScrollView: ScrollView = view.findViewById(R.id.task_scroll_view)
        val taskAppBar: AppBarLayout = view.findViewById(R.id.task_app_bar)
        val deleteTextView: TextView = view.findViewById(R.id.delete_textview)
        val deleteImageView: ImageView = view.findViewById(R.id.delete_imageview)
        val spinnerAdapter =
            ArrayAdapter(view.context, android.R.layout.simple_spinner_item, priorities)
        if(arguments!=null){
            taskId= requireArguments().getLong("taskId",-1)
            //Да, не красиво, но бд-то нет
            for (i in 0 until data.size){
                if(data[i].id==taskId){
                    taskIndex=i
                    break
                }
            }
            taskEditText.setText(data[taskIndex].text)
            prioritySpinner.setSelection(data[taskIndex].priority.toInt())
            doUntilSwitch.isChecked= data[taskIndex].deadlineDate!=null
            if(doUntilSwitch.isChecked) {
                doUntilButton.visibility = View.VISIBLE
                doUntilButton.isClickable = true
                doUntilButton.text =
                    "${data[taskIndex].deadlineDate!!.dayOfMonth}.${data[taskIndex].deadlineDate!!.monthValue}.${data[taskIndex].deadlineDate!!.year}"
            }
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner.adapter = spinnerAdapter
        taskScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                taskAppBar.elevation = 10f
            } else {
                taskAppBar.elevation = 0f
            }
        }
        if (taskId!=-1L) {
            deleteButton.isClickable = true
            deleteButton.setOnClickListener {
                Toast.makeText(
                    view.context,
                    "Deleted",
                    Toast.LENGTH_SHORT
                ).show()
                data.removeAt(taskIndex)
                closeButton.callOnClick()
            }
            deleteTextView.setTextColor(HandyFunctions.getThemeAttrColor(view.context, R.attr.color_delete_active))
            deleteImageView.setColorFilter(
                HandyFunctions.getThemeAttrColor(
                    view.context,
                    R.attr.color_delete_active
                )
            )
        } else {
            deleteButton.isClickable = false
            deleteTextView.setTextColor(HandyFunctions.getThemeAttrColor(view.context, R.attr.color_delete_inactive))
            deleteImageView.setColorFilter(
                HandyFunctions.getThemeAttrColor(
                    view.context,
                    R.attr.color_delete_inactive
                )
            )
            doUntilButton.text = "${curDate.dayOfMonth}.${curDate.monthValue}.${curDate.year}"
        }
        val datePickerListener =
            OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                doUntilButton.text = "$selectedDay.${selectedMonth + 1}.$selectedYear"
                deadlineYear = selectedYear
                deadlineMonth = selectedMonth + 1
                deadlineDay = selectedDay
            }
        val datePickerDialog = DatePickerDialog(
            view.context,
            datePickerListener,
            curDate.year,
            curDate.monthValue - 1,
            curDate.dayOfMonth
        )
        doUntilButton.setOnClickListener {

            datePickerDialog.show()
        }
        closeButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        doUntilSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                doUntilButton.visibility = View.VISIBLE
                doUntilButton.isClickable = true
            } else {
                doUntilButton.visibility = View.INVISIBLE
                doUntilButton.isClickable = false
            }
        }
        saveButton.setOnClickListener {
            //TODO isCompleted мб completed etc.
            if(taskIndex==-1){
                val x = TodoItem(
                    data[data.size-1].id+1,//Да, так нельзя!!!
                    taskEditText.text.toString(),
                    prioritySpinner.selectedItemPosition.toByte(),
                    false,
                    curDate,
                    if (doUntilSwitch.isChecked) LocalDate.of(
                        deadlineYear,
                        deadlineMonth,
                        deadlineDay
                    ) else null,
                    curDate
                )
                data.add(x)
            }else{
                val x = TodoItem(
                    taskId,
                    taskEditText.text.toString(),
                    prioritySpinner.selectedItemPosition.toByte(),
                    data[taskIndex].isCompleted,
                    data[taskIndex].creationDate,
                    if (doUntilSwitch.isChecked) LocalDate.of(
                        deadlineYear,
                        deadlineMonth,
                        deadlineDay
                    ) else null,
                    curDate
                )
                data[taskIndex]=x
            }

            closeButton.callOnClick()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.tasks.value=data
    }
}