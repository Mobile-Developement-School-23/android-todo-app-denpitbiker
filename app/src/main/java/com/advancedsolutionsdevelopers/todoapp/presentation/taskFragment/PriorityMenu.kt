package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.widget.PopupMenu
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.databinding.FragmentTaskBinding
import com.advancedsolutionsdevelopers.todoapp.utils.getThemeAttrColor

class PriorityMenu(context: Context,binding: FragmentTaskBinding): PopupMenu(context,binding.priorityPopupMenuButton) {
    var currentPriority: Int = 0
    var curPriorText: String = ""
    var wasUsed: Boolean = false
    init {
        inflate(R.menu.popup_priority_menu)
        curPriorText = this.menu.getItem(0).title.toString()
        setOnMenuItemClickListener {
            currentPriority = it.order
            when (it.itemId) {
                R.id.standard_priority_button, R.id.low_priority_button, R.id.high_priority_button -> {
                    binding.priorityTextview.text = it.title
                    curPriorText = it.title.toString()
                    wasUsed = true
                    return@setOnMenuItemClickListener true
                }

                else -> {
                    currentPriority = 0
                    curPriorText = this.menu.getItem(0).title.toString()
                    return@setOnMenuItemClickListener false
                }
            }
        }
        val itemRecall = SpannableString(this.menu.getItem(2).title)
        itemRecall.setSpan(//Высокий приоритет красного цвета
            ForegroundColorSpan(getThemeAttrColor(context, R.attr.color_delete_active)),
            0, itemRecall.length, 0
        )
        menu.removeItem(R.id.high_priority_button)
        menu.add(Menu.NONE, R.id.high_priority_button, 2, itemRecall)
    }
}