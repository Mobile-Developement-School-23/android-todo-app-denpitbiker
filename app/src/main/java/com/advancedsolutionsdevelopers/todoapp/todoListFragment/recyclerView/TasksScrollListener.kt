package com.advancedsolutionsdevelopers.todoapp.todoListFragment.recyclerView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class TasksScrollListener(private val swipeRefreshLayout: SwipeRefreshLayout) : RecyclerView.OnScrollListener() {
    private var llm: LinearLayoutManager? = null
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if(llm == null)
            llm = recyclerView.layoutManager as LinearLayoutManager
        val firstVisible = llm!!.findFirstCompletelyVisibleItemPosition()
        swipeRefreshLayout.isEnabled = firstVisible==0 || firstVisible==-1
        super.onScrollStateChanged(recyclerView, newState)
    }
}