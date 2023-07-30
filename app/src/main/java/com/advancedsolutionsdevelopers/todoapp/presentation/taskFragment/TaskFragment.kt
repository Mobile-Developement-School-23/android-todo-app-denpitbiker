package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment.particlesExplotano.GenerateEmojiSplash
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.AppTheme
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.ToDoTypography
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.redColor
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.switchBackColor
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TASK_ID_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.TimeFormatConverters
import com.advancedsolutionsdevelopers.todoapp.utils.createDateString
import com.advancedsolutionsdevelopers.todoapp.utils.dateStringToTimestamp
import com.advancedsolutionsdevelopers.todoapp.utils.pickDateAndTime
import com.advancedsolutionsdevelopers.todoapp.utils.str
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Calendar
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).activityComponent.inject(this)
        createFillViewsJob()
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
                model.isNew.value = !isEditMode
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
                priority.value = task.priority
                switchChecked.value = task.deadlineDate != null
                mDate.value =
                    converter.fromTimestamp(if (model.task.deadlineDate != null) model.task.deadlineDate!! else curDate)
                        .str()
            } else {
                mDate.value = converter.fromTimestamp(curDate).str()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun generateCalendarDialog(cal: Calendar) {
        val timestamp = createDateString(cal)
        model.mDate.value = timestamp
        model.deadlineDate = dateStringToTimestamp(timestamp)
        model.isCalendarWasOpen = true
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
        val isNew by remember { model.isNew }
        val delColor = if (isNew) switchBackColor else redColor
        Scaffold(topBar = {
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
                        Text(
                            stringResource(R.string.save), style = ToDoTypography.bodyMedium
                        )
                    }
                })
            }
        }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scroll)
                        .padding(horizontal = 10.dp)
                ) {
                    Card {
                        TextField(value = etText, onValueChange = {
                            model.etText.value = it
                        }, placeholder = {
                            Text(
                                stringResource(R.string.i_need_to_do),
                                style = ToDoTypography.bodyMedium
                            )
                        }, modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = stringResource(R.string.priority),
                        style = ToDoTypography.bodyMedium
                    )
                    OhSheet()
                    Divider()
                    Row {
                        Column {
                            Text(
                                modifier = Modifier.padding(top = 10.dp),
                                text = stringResource(R.string.do_until),
                                style = ToDoTypography.bodyMedium
                            )
                            if (switchChecked) {
                                TextButton(
                                    onClick = {
                                        requireContext().pickDateAndTime {
                                            generateCalendarDialog(it)
                                        }
                                    },
                                    contentPadding = PaddingValues(
                                        start = 0.dp,
                                        top = 0.dp,
                                        end = 0.dp,
                                        bottom = 0.dp,
                                    ),
                                    shape = RectangleShape
                                ) {
                                    Text(
                                        mDate, style = ToDoTypography.bodyMedium
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = switchChecked, onCheckedChange = {
                                model.switchChecked.value = it
                            }, colors = SwitchDefaults.colors(uncheckedTrackColor = switchBackColor)
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
                            color = delColor,
                            style = ToDoTypography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun OhSheet() {
        var showBottomSheet by remember { mutableStateOf(false) }
        val icons = arrayOf("\uD83D\uDDFF", "⬇️", "‼️")
        val sheetState = rememberModalBottomSheetState()
        val priorities = stringArrayResource(R.array.priority)
        val onTap by remember { model.priority }
        val tapOffset by remember { mutableStateOf(Offset(0f, 0f)) }
        TextButton(
            onClick = { showBottomSheet = true },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 0.dp,
                top = 0.dp,
                end = 0.dp,
                bottom = 0.dp,
            ),
            shape = RectangleShape
        ) {
            Text(
                priorities[model.priority.value.ordinal], style = ToDoTypography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                for (i in priorities.indices) {
                    val buttonColor = if (i == onTap.ordinal) {
                        switchBackColor
                    } else {
                        Color.Transparent
                    }
                    DropdownMenuItem(modifier = Modifier.background(buttonColor), text = {
                        Text(
                            priorities[i],
                            style = ToDoTypography.bodyMedium,
                        )
                        if (onTap.ordinal == i) {
                            GenerateEmojiSplash(emoji = icons[i], tapOffset, count = 25)
                        }
                    }, onClick = {
                        model.priority.value = Priority.values()[i]
                    })
                }
            }
        }
    }
}