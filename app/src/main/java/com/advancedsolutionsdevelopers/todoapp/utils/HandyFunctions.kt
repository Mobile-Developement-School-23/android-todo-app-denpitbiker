package com.advancedsolutionsdevelopers.todoapp.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.text.format.DateFormat
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.toArgb
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.ToDoApp
import com.advancedsolutionsdevelopers.todoapp.data.models.Priority
import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem
import com.advancedsolutionsdevelopers.todoapp.di.component.ApplicationComponent
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.redColor
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale
import java.util.UUID

//Получаем цвет по коду атрибута
//Да, эта функция встроена в android, но она требует аннотации @SuppressLint("RestrictedApi"),
//"Symbols marked with @RestrictTo are not considered public API,
// and may change behavior or signature arbitrarily between releases."
@ColorInt
fun getThemeAttrColor(context: Context, @AttrRes colorAttr: Int): Int {
    val array = context.obtainStyledAttributes(null, intArrayOf(colorAttr))
    return try {
        array.getColor(0, 0)
    } finally {
        array.recycle()
    }
}

//Перевод dp в float
fun Float.dpToFloat(): Float {
    val scale = Resources.getSystem().displayMetrics.density
    return (this * scale)
}

//Генератор снекбаров
fun makeSnackbar(attachTo: View, serverCode: Int) {
    val snackbar = Snackbar.make(attachTo, serverCodeToString(serverCode), Snackbar.LENGTH_LONG)
    snackbar.setTextColor(Color.WHITE)
    snackbar.isGestureInsetBottomIgnored = true
    snackbar.show()
}

fun Job.cancelIfInUse() {
    if (!isCompleted)
        cancel()
}

private fun serverCodeToString(code: Int): Int {
    return when (code) {
        600 -> R.string.failed_to_connect
        200 -> R.string.success
        500 -> R.string.server_error_try_later
        400, 404 -> R.string.synchronizing
        401 -> R.string.wrong_auth
        else -> R.string.error
    }
}

val Context.applicationComponent: ApplicationComponent
    get() = when (this) {
        is ToDoApp -> applicationComponent
        else -> (applicationContext as ToDoApp).applicationComponent
    }

fun Context.pickDateAndTime(onTimeSet: (Calendar) -> Unit) {
    val currentDateTime = Calendar.getInstance()
    val startYear = currentDateTime.get(Calendar.YEAR)
    val startMonth = currentDateTime.get(Calendar.MONTH)
    val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
    val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
    val startMinute = currentDateTime.get(Calendar.MINUTE)

    DatePickerDialog(this, R.style.DatePicker, { _, year, month, day ->
        TimePickerDialog(this, R.style.DatePicker, { _, hour, minute ->
            val pickedDateTime = Calendar.getInstance()
            pickedDateTime.set(year, month, day, hour, minute)
            onTimeSet(pickedDateTime)
        }, startHour, startMinute, DateFormat.is24HourFormat(this)).show()
    }, startYear, startMonth, startDay).show()
}

fun createDateString(day: Int, month: Int, year: Int, hour: Int, minute: Int): String =
    String.format("%02d.%02d.%04d, %02d:%02d", day, month + 1, year, hour, minute)

fun createDateString(calendar: Calendar): String = createDateString(
    calendar.get(Calendar.DAY_OF_MONTH),
    calendar.get(Calendar.MONTH),
    calendar.get(Calendar.YEAR),
    calendar.get(Calendar.HOUR_OF_DAY),
    calendar.get(Calendar.MINUTE)
)

fun LocalDateTime.str(): String {
    return this.toString().replace('T', ' ')
}

fun dateStringToTimestamp(dateString: String): Long {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy, hh:mm", Locale.getDefault())
    val date = dateFormat.parse(dateString)
    return date?.time?.div(1000) ?: 0L
}

fun Priority.toTextFormat(context: Context): String = when (this) {
    Priority.low -> context.getString(R.string.low)
    Priority.basic -> context.getString(R.string.standard)
    Priority.important -> context.getString(R.string.high)
}

fun View.actionSnackBar(
    task: TodoItem,
    delaySeconds: Int,
    action: () -> Unit,
    onCancel: () -> Unit = {}
): Snackbar {
    val mainString = context.getString(R.string.delete) + " \"" + task.text+"\""
    val job = CoroutineScope(Dispatchers.Main)
    val startColor = redColor
    val sb = Snackbar.make(
        this,
        mainString,
        Snackbar.LENGTH_INDEFINITE
    ).setTextColor(Color.WHITE)
    sb.isGestureInsetBottomIgnored = true
    sb.setAction(context.getString(R.string.cancel)) {
        job.cancel()
        onCancel()
        sb.dismiss()
    }
    sb.show()
    job.launch {
        for (i in delaySeconds downTo 1) {
            with(startColor){
                sb.setBackgroundTint(Color.rgb(red/i,green/i,blue/i))
            }
            sb.setText("$mainString in $i")
            delay(1000)
        }
        withContext(Dispatchers.IO) {
            action()
        }
        sb.dismiss()
    }
    return sb
}