package com.advancedsolutionsdevelopers.todoapp.di.component

import com.advancedsolutionsdevelopers.todoapp.di.MainActivityScope
import com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment.TaskFragment
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.TodoListFragment
import dagger.Subcomponent
//субкомпонент основной активности
@MainActivityScope
@Subcomponent
interface MainActivityComponent {
    fun inject(fragment: TodoListFragment)

    fun inject(fragment: TaskFragment)

}