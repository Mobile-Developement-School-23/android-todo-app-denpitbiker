package com.advancedsolutionsdevelopers.todoapp.data

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.advancedsolutionsdevelopers.todoapp.R
import com.google.android.material.snackbar.Snackbar
import java.util.UUID


//Класс для хранения разных функций
class HandyFunctions {
    companion object {
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
        fun makeSnackbar(attachTo: View, textResId: Int) {
            val snackbar = Snackbar.make(attachTo, textResId, Snackbar.LENGTH_LONG)
            snackbar.setTextColor(Color.WHITE)
            snackbar.isGestureInsetBottomIgnored = true
            snackbar.show()
        }
    }
}