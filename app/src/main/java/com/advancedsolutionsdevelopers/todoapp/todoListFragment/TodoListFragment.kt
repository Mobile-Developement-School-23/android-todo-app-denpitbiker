package com.advancedsolutionsdevelopers.todoapp.todoListFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.HandyFunctions.Companion.dpToFloat
import com.advancedsolutionsdevelopers.todoapp.data.HandyFunctions.Companion.makeSnackbar
import com.advancedsolutionsdevelopers.todoapp.data.TasksListViewModel
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.todoListFragment.appbar.AppBarStateChangeListener
import com.advancedsolutionsdevelopers.todoapp.todoListFragment.recyclerView.SwipeCallback
import com.advancedsolutionsdevelopers.todoapp.todoListFragment.recyclerView.TaskAdapter
import com.advancedsolutionsdevelopers.todoapp.todoListFragment.recyclerView.TasksScrollListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


//передаю привет всем, кто ...устал
class TodoListFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var coordLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var visibilityButton: ImageButton
    lateinit var fab: FloatingActionButton
    lateinit var data: LiveData<ArrayList<TodoItem>>
    lateinit var viewModel: TasksListViewModel
    private lateinit var navController: NavController
    private lateinit var tasksAdapter: TaskAdapter
    private lateinit var tasksDoneTextView: TextView
    private lateinit var swipeCallback: ItemTouchHelper.SimpleCallback
    private lateinit var appbarLayout: AppBarLayout
    private lateinit var rvBackgroundCard: CardView
    private var isCheckedTasksVisible: Boolean = true
    private val re = Regex("[0-9]")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[TasksListViewModel::class.java]
        tasksAdapter = TaskAdapter(findNavController(), viewModel)
        data = viewModel.tasks
        data.observe(requireActivity()) { value ->
            changeItemsVisibility(value)
        }
        swipeCallback = SwipeCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            tasksAdapter,
            viewModel,
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(view)
        setupListeners()
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
        recyclerView.adapter = tasksAdapter
        navController = view.findNavController()
        changeItemsVisibility(data.value!!)
    }

    private fun changeItemsVisibility(arrayList: ArrayList<TodoItem>) {
        visibilityButton.setImageResource(if (isCheckedTasksVisible) R.drawable.visible else R.drawable.invisible)
        val data = arrayListOf<TodoItem>()
        var cou = 0//считаем число выполненных задач
        for (i in arrayList) {
            if (i.isCompleted) {
                cou++
            } else if (!isCheckedTasksVisible) {
                data.add(i)
            }
        }
        tasksDoneTextView.text = re.replace(tasksDoneTextView.text, "") + cou.toString()
        //Если поменялось состояние чекбокса и мы пришли в эту функцию из обсервера, то нам незачем что-то еще перерисовывать,
        //однако, если у нас сейчас включен режим скрытия выполненных задач, то перерисовку выполнить придется
        if (!tasksAdapter.isCheckedChange or !isCheckedTasksVisible) {
            tasksAdapter.updateList(if (isCheckedTasksVisible) arrayList else data)
        }
        tasksAdapter.isCheckedChange = false
    }

    private fun findViews(parent: View) {
        toolbar = parent.findViewById(R.id.toolbar)
        swipeRefreshLayout = parent.findViewById(R.id.swipe_refresh_layout)
        coordLayout = parent.findViewById(R.id.coord_layout)
        appbarLayout = parent.findViewById(R.id.appbar_layout)
        recyclerView = parent.findViewById(R.id.rv)
        rvBackgroundCard = parent.findViewById(R.id.rv_background_cardview)
        tasksDoneTextView = parent.findViewById(R.id.tasks_done_textview)
        visibilityButton = parent.findViewById(R.id.change_visibility_button)
        fab = parent.findViewById(R.id.floatingActionButton)
    }

    private fun setupListeners() {
        appbarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                if (state == State.COLLAPSED) {
                    rvBackgroundCard.radius = 0f
                } else {
                    rvBackgroundCard.radius = 10f.dpToFloat()
                }
            }
        })
        visibilityButton.setOnClickListener {
            isCheckedTasksVisible = !isCheckedTasksVisible
            changeItemsVisibility(data.value!!)
            recyclerView.post { // Прокрутка вверх при обновлении данных
                recyclerView.smoothScrollToPosition(0)
            }
        }
        fab.setOnClickListener {
            navController.navigate(R.id.action_todoListFragment_to_taskFragment)
        }
        recyclerView.addOnScrollListener(TasksScrollListener(swipeRefreshLayout))
        swipeRefreshLayout.setOnRefreshListener {
            //TODO snackbar color + action
            makeSnackbar(coordLayout,R.string.enter_something_first)

            swipeRefreshLayout.isRefreshing=false
        }
    }
}