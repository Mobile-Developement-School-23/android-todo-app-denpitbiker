package com.advancedsolutionsdevelopers.todoapp.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.ToDoApp
import com.advancedsolutionsdevelopers.todoapp.di.component.ApplicationComponent
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job

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