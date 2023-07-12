package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.models.Priority
import com.advancedsolutionsdevelopers.todoapp.presentation.MainActivity
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.AppTheme
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.redColor
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.switchBackColor
import com.advancedsolutionsdevelopers.todoapp.utils.Constant
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.DEVICE_ID_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TASK_ID_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.TimeFormatConverters
import com.advancedsolutionsdevelopers.todoapp.utils.getThemeAttrColor
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


class TaskFragment : Fragment() {
    private var taskId: String = ""
    private var isEditMode = false
    private var firstLaunch: Boolean = true

    @Inject
    lateinit var modelFactory: TaskViewModelFactory
    private val model: TaskViewModel by lazy {
        ViewModelProvider(this, modelFactory)[TaskViewModel::class.java]
    }
    private val converter: TimeFormatConverters = TimeFormatConverters()
    private val curDate = converter.dateToTimestamp(LocalDateTime.now())
    private lateinit var deviceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceId = requireContext().getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE)
            .getString(DEVICE_ID_KEY, "")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent.inject(this)
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    TasksScreen()
                }
            }
        }
    }
    private fun createFillViewsJob() {
        taskId = requireArguments().getString(TASK_ID_KEY, "-1")
        lifecycleScope.launch {
            model.getItem(taskId).collect {
                isEditMode = it != null
                model.isNew = !isEditMode
                if (isEditMode) {
                    model.task = it!!
                    firstLaunch = false
                } else if (!firstLaunch) {
                    Toast.makeText(requireContext(), R.string.deleted, Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                fillViewsOnMode()
            }
        }
    }

    private fun fillViewsOnMode() {
        with(model) {
            if (isEditMode) {
                etText.value = task.text
                priority = task.priority
                switchChecked.value = task.deadlineDate != null
                mDate.value =
                    converter.fromTimeStampToDate(if (model.task.deadlineDate != null) model.task.deadlineDate!! else curDate)
                        .toString()
            } else {
                mDate.value = converter.fromTimeStampToDate(curDate).toString()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun generateCalendarDialog(): DatePickerDialog {
        val now = LocalDateTime.now()
        val datePickerListener = OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            model.mDate.value = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            model.deadlineDate = LocalDateTime.of(
                selectedYear, selectedMonth + 1, selectedDay, now.hour, now.minute, now.second
            )
            model.isCalendarWasOpen = true
        }
        val cur = LocalDate.now(converter.zoneId)
        return DatePickerDialog(
            requireContext(), datePickerListener, cur.year, cur.monthValue - 1, cur.dayOfMonth
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createFillViewsJob()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun TasksScreen() {
        val scroll = rememberScrollState()
        val elevation by animateDpAsState(if (scroll.value == 0) 0.dp else 10.dp)
        val switchChecked by remember { model.switchChecked }
        val etText by remember { model.etText }
        val mDate by remember { model.mDate }
        val delColor = if (isEditMode) redColor else switchBackColor
        Scaffold(
            topBar = {
                Surface(shadowElevation = elevation) {
                    TopAppBar(title = {}, navigationIcon = {
                        IconButton(onClick = { requireActivity().supportFragmentManager.popBackStack() }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }, actions = {
                        TextButton(onClick = {
                            if (model.etText.value == "") {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.enter_something_first,
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@TextButton
                            }
                            model.saveItem(isEditMode)
                            requireActivity().supportFragmentManager.popBackStack()
                        }) {
                            Text(stringResource(R.string.save))
                        }
                    }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scroll)
                        .padding(horizontal = 10.dp)
                ) {
                    Card {
                        TextField(
                            value = etText,
                            onValueChange = {
                                model.etText.value = it
                            },
                            placeholder = { Text(stringResource(R.string.i_need_to_do)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Text(text = stringResource(R.string.priority))
                    DropDownMenu()
                    Divider()
                    Row {
                        Column {
                            Text(text = stringResource(R.string.do_until))
                            if (switchChecked) {
                                TextButton(
                                    onClick = { generateCalendarDialog().show() },
                                    contentPadding = PaddingValues(
                                        start = 0.dp,
                                        top = 0.dp,
                                        end = 0.dp,
                                        bottom = 0.dp,
                                    ),
                                    shape = RectangleShape
                                ) {
                                    Text(mDate)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = switchChecked,
                            onCheckedChange = {
                                model.switchChecked.value = it
                            },
                            colors = SwitchDefaults.colors(uncheckedTrackColor = switchBackColor)
                        )
                    }
                    Divider()
                    Row(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .clickable(enabled = isEditMode, onClick = { model.deleteItem() })
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = delColor
                        )
                        Text(
                            text = stringResource(R.string.delete),
                            color = delColor
                        )
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun DropDownMenu() {
        val priorities = stringArrayResource(R.array.priority)
        var expanded by remember { mutableStateOf(false) }
        var menuText by remember { mutableStateOf(priorities[model.priority.ordinal]) }
        Box {
            TextButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 0.dp,
                    top = 0.dp,
                    end = 0.dp,
                    bottom = 0.dp,
                ),
                shape = RectangleShape
            ) {
                Text(menuText)
                Spacer(modifier = Modifier.weight(1f))
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                for (i in 0..2) {
                    DropdownMenuItem(
                        text = { Text(priorities[i]) },
                        onClick = {
                            expanded = false
                            menuText = priorities[i]
                            model.priority = Priority.values()[i]
                        }
                    )
                }
            }
        }
    }
}