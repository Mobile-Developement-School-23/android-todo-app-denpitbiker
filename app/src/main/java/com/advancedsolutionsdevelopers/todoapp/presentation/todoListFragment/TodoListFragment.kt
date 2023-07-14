package com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.network.PassportAuthContract
import com.advancedsolutionsdevelopers.todoapp.databinding.FragmentTodoListBinding
import com.advancedsolutionsdevelopers.todoapp.presentation.MainActivity
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView.NavigationMode
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView.SwipeCallback
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.recyclerView.TaskAdapter
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.SHOW_NOTIFICATIONS_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TOKEN_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.cancelIfInUse
import com.advancedsolutionsdevelopers.todoapp.utils.makeSnackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


class TodoListFragment : Fragment() {
    @Inject
    lateinit var callback: RegisterResultCallback

    @Inject
    lateinit var modelFactory: TasksListViewModelFactory

    @Inject
    lateinit var sp: SharedPreferences
    private lateinit var navController: NavController
    private var tasksAdapter: TaskAdapter? = null
    private var swipeCallback: SwipeCallback? = null
    private val viewModel: TasksListViewModel by lazy {
        ViewModelProvider(this, modelFactory)[TasksListViewModel::class.java]
    }
    private var startForResult: ActivityResultLauncher<Any?>? = null
    private var isCheckedTasksVisible: Boolean = true
    private var isAuthorized = false
    private var tasksJob: Job? = null
    private val re = Regex("[0-9]")
    private var _binding: FragmentTodoListBinding? = null
    private val binding: FragmentTodoListBinding
        get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).activityComponent.inject(this)
        startForResult = registerForActivityResult(
            PassportAuthContract(), callback
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        isAuthorized = sp.contains(TOKEN_KEY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        if (!sp.contains(SHOW_NOTIFICATIONS_KEY)) {
            navController.navigate(R.id.action_todoListFragment_to_notificationPermFragment)
        }
        tasksAdapter = TaskAdapter()
        swipeCallback = SwipeCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            tasksAdapter!!,
            requireContext(),
            binding.swipeRefreshLayout,
            binding.coordLayout
        )
        setupListeners()
        ItemTouchHelper(swipeCallback!!).attachToRecyclerView(binding.rv)
        binding.rv.adapter = tasksAdapter
        changeItemsVisibility()
        observeCompletedCount()
        observeServerCodes()
        observeTasks()
        observeNavigation()
    }

    private fun observeNavigation() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationState.collect {
                    if (it.mode != NavigationMode.None) {
                        navController.navigate(
                            R.id.action_todoListFragment_to_taskFragment, it.bundle
                        )
                        viewModel.onNavigated()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeCompletedCount() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.completedTasksCount.collect {
                    binding.tasksDoneTextview.text =
                        re.replace(binding.tasksDoneTextview.text, "") + it.toString()
                }
            }
        }
    }

    private fun observeServerCodes() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.serverCodes.collect {
                    if (isAuthorized) {
                        makeSnackbar(binding.coordLayout, it)
                    }
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun observeTasks() {
        tasksJob?.cancelIfInUse()
        tasksJob = lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (isCheckedTasksVisible) {
                    viewModel.allTasks.collect {
                        tasksAdapter!!.submitList(it)
                    }
                } else {
                    viewModel.uncompletedTasks.collect {
                        tasksAdapter!!.submitList(it)
                    }
                }
            }
        }
    }

    private fun changeItemsVisibility() {
        binding.changeVisibilityButton.setImageResource(
            if (isCheckedTasksVisible) R.drawable.visible else R.drawable.invisible
        )
        observeTasks()
    }

    private fun changeInternetMode() {
        if (isAuthorized) {
            isAuthorized = false
            viewModel.changeConnectionMode(dropTable = !isAuthorized)
        } else startForResult!!.launch(null)
    }

    private fun setupListeners() {
        binding.changeVisibilityButton.setOnClickListener {
            isCheckedTasksVisible = !isCheckedTasksVisible
            changeItemsVisibility()
        }
        binding.settingsButton.setOnClickListener {
            //changeInternetMode()
            navController.navigate(R.id.action_todoListFragment_to_settingsFragment)
        }
        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.action_todoListFragment_to_taskFragment, Bundle())
        }
        binding.appbarLayout.addOnOffsetChangedListener(
            AppBarStateChangeListener(
                swipeCallback!!, binding.swipeRefreshLayout, binding.rvBackgroundCardview
            )
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (!isAuthorized) startForResult!!.launch(null)
            if (isAuthorized) viewModel.syncWithServer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        swipeCallback = null
        tasksAdapter = null
        _binding = null
    }
}