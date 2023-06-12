package com.advancedsolutionsdevelopers.todoapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import java.time.LocalDate


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        val curDate = LocalDate.now()
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
        taskScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                taskAppBar.elevation = 10f
            } else {
                taskAppBar.elevation = 0f
            }
        }
        // TODO: Параметр, который будет влиять на цвет и поведение
        if (true) {
            deleteButton.isClickable = true
            deleteButton.setOnClickListener {
                Toast.makeText(
                    view.context,
                    "Deleted",
                    Toast.LENGTH_SHORT
                ).show()
                closeButton.callOnClick()
            }
            deleteTextView.setTextColor(getThemeAttrColor(view.context, R.attr.color_delete_active))
            deleteImageView.setColorFilter(
                getThemeAttrColor(
                    view.context,
                    R.attr.color_delete_active
                )
            )
        } else {
            deleteButton.isClickable = false
        }
        val datePickerListener =
            OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                doUntilButton.text = "$selectedDay.${selectedMonth + 1}.$selectedYear"
            }
        val datePickerDialog = DatePickerDialog(
            view.context,
            datePickerListener,
            curDate.year,
            curDate.monthValue - 1,
            curDate.dayOfMonth
        )

        doUntilButton.text = "${curDate.dayOfMonth}.${curDate.monthValue}.${curDate.year}"
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
    }

    @ColorInt
    private fun getThemeAttrColor(context: Context, @AttrRes colorAttr: Int): Int {
        val array = context.obtainStyledAttributes(null, intArrayOf(colorAttr))
        return try {
            array.getColor(0, 0)
        } finally {
            array.recycle()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TaskFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}