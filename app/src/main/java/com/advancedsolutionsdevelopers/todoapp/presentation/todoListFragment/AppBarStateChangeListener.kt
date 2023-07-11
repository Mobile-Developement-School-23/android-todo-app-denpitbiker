package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment

import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.advancedsolutionsdevelopers.todoapp.utils.dpToFloat
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView.SwipeCallback
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
                onStateChanged(State.EXPANDED)
            }
            State.EXPANDED
        } else if (abs(i) >= appBarLayout.totalScrollRange) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(State.COLLAPSED)
            }
            State.COLLAPSED
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(State.IDLE)
            }
            State.IDLE
        }
    }

    private fun onStateChanged(state: State) {
        swipeCallback.isAppbarExpanded = state == State.EXPANDED
        swipeRefreshLayout.isEnabled = state == State.EXPANDED
        if (state == State.COLLAPSED) {
            rvBackgroundCard.radius = 0f
        } else {
            rvBackgroundCard.radius = 10f.dpToFloat()
        }
    }
}