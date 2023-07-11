package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.databinding.FragmentTaskBinding
import com.advancedsolutionsdevelopers.todoapp.utils.getThemeAttrColor

class PriorityMenu(context: Context, private val binding: FragmentTaskBinding) :
    PopupMenu(context, binding.priorityPopupMenuButton), PopupMenu.OnMenuItemClickListener {
    var currentPriority: Int = 0
    var curPriorText: String = ""
    var wasUsed: Boolean = false

    init {
        inflate(R.menu.popup_priority_menu)
        curPriorText = this.menu.getItem(0).title.toString()
        setOnMenuItemClickListener(this)
        val itemRecall = SpannableString(this.menu.getItem(2).title)
        itemRecall.setSpan(//Высокий приоритет красного цвета
            ForegroundColorSpan(getThemeAttrColor(context, R.attr.color_delete_active)),
            0, itemRecall.length, 0
        )
        menu.removeItem(R.id.high_priority_button)
        menu.add(Menu.NONE, R.id.high_priority_button, 2, itemRecall)
    }

    override fun onMenuItemClick(it: MenuItem?): Boolean {
        currentPriority = it!!.order
        return when (it.itemId) {
            R.id.standard_priority_button, R.id.low_priority_button, R.id.high_priority_button -> {
                binding.priorityTextview.text = it.title
                curPriorText = it.title.toString()
                wasUsed = true
                true
            }

            else -> {
                currentPriority = 0
                curPriorText = this.menu.getItem(0).title.toString()
                false
            }
        }
    }
}