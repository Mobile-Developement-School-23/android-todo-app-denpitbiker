package com.advancedsolutionsdevelopers.todoapp.todoListFragment.appbar

import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.advancedsolutionsdevelopers.todoapp.data.HandyFunctions.Companion.dpToFloat
import com.advancedsolutionsdevelopers.todoapp.todoListFragment.recyclerView.SwipeCallback
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlin.math.abs


class AppBarStateChangeListener(
    private val swipeCallback: SwipeCallback,
    private val swipeRefreshLayout: SwipeRefreshLayout,
    private val rvBackgroundCard: CardView
) : OnOffsetChangedListener {
    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    private var mCurrentState = State.IDLE

    //Отслеживаем состояние AppBar
    //(Необходимо для изменения интерфейса при его сжатии)
    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        mCurrentState = if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED)
            }
            State.EXPANDED
        } else if (abs(i) >= appBarLayout.totalScrollRange) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED)
            }
            State.COLLAPSED
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE)
            }
            State.IDLE
        }
    }

    private fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
        swipeCallback.isAppbarExpanded = state == State.EXPANDED
        swipeRefreshLayout.isEnabled = state == State.EXPANDED
        if (state == State.COLLAPSED) {
            rvBackgroundCard.radius = 0f
        } else {
            rvBackgroundCard.radius = 10f.dpToFloat()
        }
    }
}