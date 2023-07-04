package com.advancedsolutionsdevelopers.todoapp.todoListFragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.Constant.sp_name
import com.advancedsolutionsdevelopers.todoapp.data.Constant.token_key
import com.advancedsolutionsdevelopers.todoapp.data.HandyFunctions.Companion.makeSnackbar
import com.advancedsolutionsdevelopers.todoapp.data.TasksListViewModel
import com.advancedsolutionsdevelopers.todoapp.data.TodoItem
import com.advancedsolutionsdevelopers.todoapp.data.TodoItemsRepository
import com.advancedsolutionsdevelopers.todoapp.network.PassportAuthContract
import com.advancedsolutionsdevelopers.todoapp.todoListFragment.appbar.AppBarStateChangeListener
import com.advancedsolutionsdevelopers.todoapp.todoListFragment.recyclerView.SwipeCallback
import com.advancedsolutionsdevelopers.todoapp.todoListFragment.recyclerView.TaskAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Job

class TodoListFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var coordLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var visibilityButton: ImageButton
    lateinit var internetButton: ImageButton
    lateinit var fab: FloatingActionButton
    private val viewModel: TasksListViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var tasksAdapter: TaskAdapter
    private lateinit var tasksDoneTextView: TextView
    private lateinit var swipeCallback: SwipeCallback
    private lateinit var appbarLayout: AppBarLayout
    private lateinit var rvBackgroundCard: CardView
    private lateinit var sharedPrefs: SharedPreferences
    private var startForResult: ActivityResultLauncher<Any?>? = null
    private var isCheckedTasksVisible: Boolean = true
    private var isAuthorized = false
    private val re = Regex("[0-9]")
    private var codesJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tasksAdapter = TaskAdapter(findNavController(),lifecycleScope)
        sharedPrefs = requireContext().getSharedPreferences(sp_name, Context.MODE_PRIVATE)
        registerAuthActivity()
    }

    fun checkForAuth() {
        if (!isAuthorized) {
            startForResult!!.launch(null)
        }
    }

    private fun registerAuthActivity() {
        startForResult = registerForActivityResult(
            PassportAuthContract(),
            RegisterResultCallback(requireContext(),lifecycleScope)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        isAuthorized = sharedPrefs.contains(token_key)
        internetButton.setImageResource(if (isAuthorized) R.drawable.offline else R.drawable.online)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(view)
        internetButton.setImageResource(if (isAuthorized) R.drawable.offline else R.drawable.online)
        swipeCallback = SwipeCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            tasksAdapter, requireContext(), swipeRefreshLayout,lifecycleScope
        )
        setupListeners()
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
        recyclerView.adapter = tasksAdapter
        navController = view.findNavController()
        changeItemsVisibility()
        observeCompletedCount()
        observeServerCodes()
        observeTasks()
    }

    private fun observeCompletedCount() {
        viewModel.subscribeOnCompletedTasksCount()
        viewModel.completedTasksCount.observe(viewLifecycleOwner) {
            tasksDoneTextView.text = re.replace(tasksDoneTextView.text, "") + it.toString()
        }
    }

    private fun observeServerCodes() {
        viewModel.subscribeOnServerCodes()
        viewModel.serverCodes.observe(viewLifecycleOwner){
            when (it) {
                600 -> makeSnackbar(coordLayout, R.string.failed_to_connect)
                200 -> makeSnackbar(coordLayout, R.string.success)
                500 -> makeSnackbar(coordLayout, R.string.server_error_try_later)
                400, 404 -> makeSnackbar(coordLayout, R.string.synchronizing)
                401 -> makeSnackbar(coordLayout, R.string.wrong_auth)
            }
        }
    }

    private fun observeTasks() {
        if (isCheckedTasksVisible) {
            viewModel.unsubscribeOnUncompletedTasksChanges()
            viewModel.subscribeOnTasksChanges()
            viewModel.tasks.observe(viewLifecycleOwner) {
                updateRVDataAndScrollUp(it)
            }
        } else {
            viewModel.unsubscribeOnTasksChanges()
            viewModel.subscribeOnUncompletedTasksChanges()
            viewModel.tasksUncompleted.observe(viewLifecycleOwner) {
                updateRVDataAndScrollUp(it)
            }
        }
    }

    private fun updateRVDataAndScrollUp(tasks: List<TodoItem>) {
        tasksAdapter.updateList(tasks)
        recyclerView.smoothScrollToPosition(0)
    }

    override fun onPause() {
        super.onPause()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun changeItemsVisibility() {
        visibilityButton.setImageResource(if (isCheckedTasksVisible) R.drawable.visible else R.drawable.invisible)
        observeTasks()
    }

    private fun changeInternetMode() {
        if (isAuthorized) {
            isAuthorized = false
            TodoItemsRepository.changeConnectionMode(true)
            internetButton.setImageResource(R.drawable.online)
        } else {
            startForResult!!.launch(null)
        }
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
        internetButton = parent.findViewById(R.id.internet_button)
        fab = parent.findViewById(R.id.floatingActionButton)
    }

    private fun setupListeners() {
        setUpButtonListeners()
        appbarLayout.addOnOffsetChangedListener(
            AppBarStateChangeListener(
                swipeCallback,
                swipeRefreshLayout,
                rvBackgroundCard
            )
        )
        swipeRefreshLayout.setOnRefreshListener {
            checkForAuth()
            viewModel.syncWithServer()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setUpButtonListeners() {
        visibilityButton.setOnClickListener {
            isCheckedTasksVisible = !isCheckedTasksVisible
            changeItemsVisibility()
        }
        internetButton.setOnClickListener {
            changeInternetMode()
        }
        fab.setOnClickListener {
            navController.navigate(R.id.action_todoListFragment_to_taskFragment)
        }
    }

    private fun unsubscribeViewModelFlows() {
        viewModel.unsubscribeOnTasksChanges()
        viewModel.unsubscribeOnCompletedTasksCount()
        viewModel.unsubscribeOnUncompletedTasksChanges()
        viewModel.unsubscribeOnServerCodes()
    }

    override fun onDestroy() {
        super.onDestroy()
        codesJob?.cancel()
        unsubscribeViewModelFlows()
    }
}