package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.utils.actionSnackBar
import com.advancedsolutionsdevelopers.todoapp.utils.getThemeAttrColor
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

class SwipeCallback(
    dragDirs: Int,
    swipeDirs: Int,
    private val tasksAdapter: TaskAdapter,
    private val context: Context,
    private val swipeRef: SwipeRefreshLayout,
    private val coordinatorLayout: CoordinatorLayout
) : ItemTouchHelper.SimpleCallback(
    dragDirs, swipeDirs
) {
    private val textMargin = context.resources.getDimension(R.dimen.text_margin).roundToInt()
    private val snackbars = hashMapOf<Snackbar, () -> Unit>()
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
            (viewHolder as TaskViewHolder).isCompleteChBox.isChecked = true
            viewHolder.isCompleteChBox.callOnClick()
        } else {
            if (viewHolder.adapterPosition < tasksAdapter.currentList.size - 1) {
                val item = tasksAdapter.currentList[viewHolder.adapterPosition]
                cancelBars()
                snackbars[coordinatorLayout.actionSnackBar(item.todoItem, 5, item.onDelete) {
                    notifyItemChanged(viewHolder as TaskViewHolder)
                }] = item.onDelete
            }
        }
    }

    private fun notifyItemChanged(viewHolder: TaskViewHolder) {
        tasksAdapter.notifyItemChanged(viewHolder.adapterPosition)
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
        if (viewHolder.adapterPosition == tasksAdapter.currentList.size - 1) {
            return//Когда пытаемся свайпнуть "Add new task"
        }
        if (viewHolder.itemView.x != 0f) {
            swipeRef.isEnabled = false
        } else {
            swipeRef.isEnabled = isAppbarExpanded
        }
        val isDeleteSwipe = viewHolder.itemView.x < 0//Режим отрисовки бэкграунда при свайпе
        val icon = AppCompatResources.getDrawable(
            context, if (isDeleteSwipe) R.drawable.delete else R.drawable.check
        )
        drawOnCanvas(c, isDeleteSwipe, viewHolder.itemView, icon!!, dX)
        icon.draw(c)
        super.onChildDraw(
            c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
        )

    }

    private fun drawOnCanvas(c: Canvas, isDelSwipe: Boolean, v: View, icon: Drawable, dX: Float) {
        val verticalMargin = (v.bottom - v.top - icon.intrinsicHeight) / 2
        val color = getThemeAttrColor(
            context, if (isDelSwipe) R.attr.color_delete_active else R.attr.base_green
        )
        if (isDelSwipe) {
            drawDelSwipe(c, v, dX, icon, verticalMargin, color)
        } else {
            drawCheckSwipe(c, v, dX, icon, verticalMargin, color)
        }
    }

    private fun cancelBars() {
        for (i in snackbars.keys) {
            i.dismiss()
            snackbars[i]!!.invoke()
        }
        snackbars.clear()
    }

    private fun drawDelSwipe(
        c: Canvas, v: View, dX: Float, icon: Drawable, verticalMargin: Int, color: Int
    ) {
        c.clipRect(
            v.right.toFloat(), v.top.toFloat(), v.right.toFloat() + dX, v.bottom.toFloat()
        )
        c.drawColor(color)
        icon.bounds = Rect(
            v.right - icon.intrinsicWidth - textMargin,
            v.top + verticalMargin,
            v.right - textMargin,
            v.bottom - verticalMargin
        )
    }

    private fun drawCheckSwipe(
        c: Canvas, v: View, dX: Float, icon: Drawable, verticalMargin: Int, color: Int
    ) {
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