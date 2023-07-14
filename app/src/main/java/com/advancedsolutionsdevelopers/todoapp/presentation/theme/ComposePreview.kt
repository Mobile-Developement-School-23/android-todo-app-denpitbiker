package com.advancedsolutionsdevelopers.todoapp.presentation.theme

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment.particlesExplotano.GenerateEmojiSplash

class ComposePreview {
    @OptIn(ExperimentalTextApi::class)
    @Preview
    @Composable
    private fun AppThemePreview() {
        AppTheme {
            val textMeasurer = rememberTextMeasurer()
            val style = TextStyle(color = Color.White)
            val typo = MaterialTheme.typography
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                drawRect(
                    color = lightPrimary,
                    size = canvasQuadrantSize
                )
                drawText(textMeasurer, "lightPrimary")
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                drawRect(
                    color = lightOnPrimary,
                    size = canvasQuadrantSize,
                    topLeft = Offset(canvasQuadrantSize.width, 0f)
                )
                drawText(textMeasurer, "lightOnPrimary", Offset(canvasQuadrantSize.width, 0f))
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                drawRect(
                    color = lightBackground,
                    size = canvasQuadrantSize,
                    topLeft = Offset(canvasQuadrantSize.width * 3, 0f)
                )
                drawText(
                    textMeasurer,
                    "light\nBackground",
                    Offset(canvasQuadrantSize.width * 3, 0f)
                )
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                drawRect(
                    color = lightSurface,
                    size = canvasQuadrantSize,
                    topLeft = Offset(canvasQuadrantSize.width * 2, 0f)
                )
                drawText(textMeasurer, "lightSurface", Offset(canvasQuadrantSize.width * 2, 0f))
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                drawRect(
                    color = lightSurfaceVariant,
                    size = canvasQuadrantSize,
                    topLeft = Offset(0f, canvasQuadrantSize.height)

                )
                drawText(
                    textMeasurer,
                    "lightSurface\nVariant",
                    Offset(0f, canvasQuadrantSize.height)
                )
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                drawRect(
                    color = lightOutline,
                    size = canvasQuadrantSize,
                    topLeft = Offset(canvasQuadrantSize.width, canvasQuadrantSize.height)
                )
                drawText(
                    textMeasurer,
                    "lightOutline",
                    Offset(canvasQuadrantSize.width, canvasQuadrantSize.height)
                )
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                val offset = Offset(canvasQuadrantSize.width * 2, canvasQuadrantSize.height)
                drawRect(
                    color = darkPrimary,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, "darkPrimary", offset, style = style)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                val offset = Offset(canvasQuadrantSize.width * 3, canvasQuadrantSize.height)
                drawRect(
                    color = darkOnPrimary,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, "darkOnPrimary", offset, style = style)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                val offset = Offset(canvasQuadrantSize.width * 0, canvasQuadrantSize.height * 2)
                drawRect(
                    color = darkSurface,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, "darkSurface", offset, style = style)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                val offset = Offset(canvasQuadrantSize.width, canvasQuadrantSize.height * 2)
                drawRect(
                    color = darkBackground,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, "dark\nBackground", offset, style = style)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                val offset = Offset(canvasQuadrantSize.width * 2, canvasQuadrantSize.height * 2)
                drawRect(
                    color = darkSurfaceVariant,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, "dark\nSurfaceVariant", offset, style = style)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                val offset = Offset(canvasQuadrantSize.width * 3, canvasQuadrantSize.height * 2)
                drawRect(
                    color = darkOutline,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, "darkOutline", offset, style = style)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                val offset = Offset(canvasQuadrantSize.width * 0, canvasQuadrantSize.height * 3)
                drawRect(
                    color = switchBackColor,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, "switch\nBackColor", offset, style = style)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                val offset = Offset(canvasQuadrantSize.width * 1, canvasQuadrantSize.height * 3)
                drawRect(
                    color = redColor,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, "redColor", offset, style = style)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width / 4F, size.height / 8F)
                val offset = Offset(canvasQuadrantSize.width * 2, canvasQuadrantSize.height * 3)
                drawRect(
                    color = greenColor,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, "greenColor", offset, style = style)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width, size.height / 16F)
                val offset = Offset(0f, canvasQuadrantSize.height * 8)
                drawRect(
                    color = lightSurface,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, " Large Title", offset, style = typo.titleLarge)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width, size.height / 16F)
                val offset = Offset(0f, canvasQuadrantSize.height * 9)
                drawRect(
                    color = lightSurface,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, " Title", offset, style = typo.titleMedium)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width, size.height / 16F)
                val offset = Offset(0f, canvasQuadrantSize.height * 10)
                drawRect(
                    color = lightSurface,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, " Subtitle", offset, style = typo.titleSmall)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width, size.height / 16F)
                val offset = Offset(0f, canvasQuadrantSize.height * 11)
                drawRect(
                    color = lightSurface,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, " Body", offset, style = typo.bodyMedium)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasQuadrantSize = Size(size.width, size.height / 16F)
                val offset = Offset(0f, canvasQuadrantSize.height * 12)
                drawRect(
                    color = lightSurface,
                    size = canvasQuadrantSize,
                    topLeft = offset
                )
                drawText(textMeasurer, " Button", offset, style = typo.labelLarge)
            }
        }
    }

    @Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    private fun PreviewTasksScreenWithTheme() {
        AppTheme {
            TasksScreen()
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun TasksScreen() {
        val mockedIsEditMode = false//Exists in fragment
        val mockedEt = mutableStateOf("")//Exists in TaskViewModel
        val mockedSwitchChecked = mutableStateOf(false)//Exists in TaskViewModel
        val scroll = rememberScrollState()
        val elevation by animateDpAsState(if (scroll.value == 0) 0.dp else 10.dp)
        val switchChecked by remember { mockedSwitchChecked/*model.switchChecked*/ }
        val etText by remember { mockedEt/*model.etText*/ }
        val mDate by remember { mutableStateOf("12-07-2023")/*model.mDate*/ }
        val delColor = if (mockedIsEditMode) redColor else switchBackColor
        Scaffold(
            topBar = {
                Surface(shadowElevation = elevation) {
                    TopAppBar(title = {}, navigationIcon = {
                        IconButton(onClick = { /*requireActivity().supportFragmentManager.popBackStack()*/ }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }, actions = {
                        TextButton(onClick = {
                            if (mockedEt.value == "") {
                                /*Toast.makeText(
                                    requireContext(),
                                    R.string.enter_something_first,
                                    Toast.LENGTH_SHORT
                                ).show()*/
                                return@TextButton
                            }
                            /*model.saveItem(isEditMode)
                            requireActivity().supportFragmentManager.popBackStack()*/
                        }) {
                            Text(stringResource(R.string.save), style = ToDoTypography.bodyMedium)
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
                                mockedEt.value = it
                            },
                            placeholder = { Text(stringResource(R.string.i_need_to_do)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Text(
                        text = stringResource(R.string.priority),
                        modifier = Modifier.padding(top = 10.dp),
                        style = ToDoTypography.bodyMedium
                    )
                    OhSheet()
                    Divider()
                    Row {
                        Column {
                            Text(
                                text = stringResource(R.string.do_until),
                                modifier = Modifier.padding(top = 10.dp),
                                style = ToDoTypography.bodyMedium
                            )
                            if (switchChecked) {
                                TextButton(
                                    onClick = { /*generateCalendarDialog().show()*/ },
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
                                mockedSwitchChecked.value = it
                            },
                            colors = SwitchDefaults.colors(uncheckedTrackColor = switchBackColor)
                        )
                    }
                    Divider()
                    Row(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .clickable(
                                enabled = mockedIsEditMode,
                                onClick = { /*model.deleteItem()*/ })
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = delColor
                        )
                        Text(
                            text = stringResource(R.string.delete),
                            color = delColor, style = ToDoTypography.bodyMedium
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
        val onTap by remember { mutableStateOf(0)/*model.priority*/ }
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
                priorities[onTap/*model.priority.value.ordinal*/], style = ToDoTypography.bodyMedium
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
                    val buttonColor = if (i == onTap) {
                        switchBackColor
                    } else {
                        Color.Transparent
                    }
                    DropdownMenuItem(modifier = Modifier.background(buttonColor), text = {
                        Text(
                            priorities[i],
                            style = ToDoTypography.bodyMedium,
                        )
                        if (onTap == i) {
                            GenerateEmojiSplash(emoji = icons[i], tapOffset, count = 25)
                        }
                    }, onClick = {
                        //model.priority.value = Priority.values()[i]
                    })
                }
            }
        }
    }
}