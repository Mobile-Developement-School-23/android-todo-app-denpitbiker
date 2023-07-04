package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.utils.getThemeAttrColor
import kotlin.math.roundToInt

class SwipeCallback(
    dragDirs: Int,
    swipeDirs: Int,
    private val tasksAdapter: TaskAdapter,
    private val context: Context,
    private val swipeRef: SwipeRefreshLayout) : ItemTouchHelper.SimpleCallback(
    dragDirs,
    swipeDirs
) {
    var isAppbarExpanded = false
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.RIGHT) {
            (viewHolder as TaskViewHolder).isCompleteCheckbox.isChecked = true
            viewHolder.isCompleteCheckbox.callOnClick()
        } else {
            if (viewHolder.adapterPosition < tasksAdapter.currentList.size-1) {
                tasksAdapter.currentList[viewHolder.adapterPosition].onDelete()
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder.adapterPosition == tasksAdapter.currentList.size-1) {
            return//Когда пытаемся свайпнуть "Add new task"
        }
        if (viewHolder.itemView.x != 0f) {
            swipeRef.isEnabled = false
        } else {
            swipeRef.isEnabled = isAppbarExpanded
        }
        val isDeleteSwipe = viewHolder.itemView.x < 0//Режим отрисовки бэкграунда при свайпе

        val icon = AppCompatResources.getDrawable(
            context,
            if (isDeleteSwipe) R.drawable.delete else R.drawable.check
        )
        drawOnCanvas(c, isDeleteSwipe, viewHolder.itemView, icon!!, dX)
        icon.draw(c)
        super.onChildDraw(
            c, recyclerView, viewHolder,
            dX, dY, actionState, isCurrentlyActive
        )

    }

    private fun drawOnCanvas(c: Canvas, isDelSwipe: Boolean, v: View, icon: Drawable, dX: Float) {
        val verticalMargin = (v.bottom - v.top - icon.intrinsicHeight) / 2
        val textMargin = context.resources.getDimension(R.dimen.text_margin).roundToInt()
        val color = getThemeAttrColor(
            context,
            if (isDelSwipe) R.attr.color_delete_active else R.attr.base_green
        )
        if (isDelSwipe) {
            c.clipRect(
                v.right.toFloat(),
                v.top.toFloat(),
                v.right.toFloat() + dX,
                v.bottom.toFloat()
            )
            c.drawColor(color)
            icon.bounds = Rect(
                v.right - icon.intrinsicWidth - textMargin,
                v.top + verticalMargin,
                v.right - textMargin,
                v.bottom - verticalMargin
            )
        } else {
            c.clipRect(0f, v.top.toFloat(), dX, v.bottom.toFloat())
            c.drawColor(color)
            icon.bounds = Rect(
                textMargin,
                v.top + verticalMargin,
                textMargin + icon.intrinsicWidth,
                v.bottom - verticalMargin
            )
        }
    }
}