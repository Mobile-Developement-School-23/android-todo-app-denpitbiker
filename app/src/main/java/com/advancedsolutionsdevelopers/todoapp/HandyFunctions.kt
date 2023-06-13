package com.advancedsolutionsdevelopers.todoapp

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

//Класс для хранения разных функций
class HandyFunctions {
    companion object{
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
    }
}