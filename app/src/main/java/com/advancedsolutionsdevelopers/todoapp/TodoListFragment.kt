package com.advancedsolutionsdevelopers.todoapp

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.advancedsolutionsdevelopers.todoapp.recyclerView.TaskAdapter
import com.advancedsolutionsdevelopers.todoapp.recyclerView.TaskViewHolder
import com.advancedsolutionsdevelopers.todoapp.recyclerView.TodoItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.roundToInt

class TodoListFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var visibilityButton: ImageButton
    lateinit var fab: FloatingActionButton
    lateinit var data: LiveData<ArrayList<TodoItem>>
    lateinit var viewModel: TasksListViewModel
    private lateinit var navController: NavController
    private lateinit var tasksAdapter: TaskAdapter
    private lateinit var tasksDoneTextView: TextView
    private lateinit var swipeToDeleteCallback:ItemTouchHelper.SimpleCallback
    private lateinit var swipeToCheckCallback:ItemTouchHelper.SimpleCallback
    private var isVisible: Boolean = true
    private val re = Regex("[0-9]")
    fun RecyclerView.runWhenReady(action: () -> Unit) {
        val globalLayoutListener = object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                action()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[TasksListViewModel::class.java]
        tasksAdapter = TaskAdapter(findNavController(), viewModel)
        data = viewModel.tasks
        data.observe(
            requireActivity()
        ) { value ->
            changeItemsVisibility(value)
        }
        swipeToDeleteCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                tasksAdapter.isCheckedChange=true
                viewModel.deleteItem(viewHolder.adapterPosition)
                tasksAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                //TODO дописать
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
                //TODO Положение мусорки
                val trashBinIcon = getDrawable(requireContext(),R.drawable.delete)
                c.clipRect(viewHolder.itemView.right.toFloat(), viewHolder.itemView.top.toFloat(),
                    viewHolder.itemView.right.toFloat()+dX, viewHolder.itemView.bottom.toFloat())

                c.drawColor( HandyFunctions.getThemeAttrColor(
                    requireActivity(),
                    R.attr.color_delete_active
                )
                )
                val textMargin = resources.getDimension(R.dimen.text_margin)
                    .roundToInt()
                trashBinIcon!!.bounds = Rect(
                    viewHolder.itemView.right -trashBinIcon.intrinsicWidth- textMargin,
                    viewHolder.itemView.top + textMargin,
                    viewHolder.itemView.right - textMargin,
                    viewHolder.itemView.top + trashBinIcon.intrinsicHeight
                            + textMargin
                )
                trashBinIcon.draw(c)
                super.onChildDraw(c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive)

            }
        }
        swipeToCheckCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (viewHolder as TaskViewHolder).isCompleteCheckbox.isChecked=true
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
                //TODO Положение галочки
                val checkIcon = getDrawable(requireContext(),R.drawable.check)
                c.clipRect(0f, viewHolder.itemView.top.toFloat(),
                    dX, viewHolder.itemView.bottom.toFloat())
                c.drawColor( HandyFunctions.getThemeAttrColor(
                    requireActivity(),
                    R.attr.base_green
                )
                )
                val textMargin = resources.getDimension(R.dimen.text_margin)
                    .roundToInt()
                checkIcon!!.bounds = Rect(
                    textMargin,
                    viewHolder.itemView.top + textMargin,
                    textMargin + checkIcon.intrinsicWidth,
                    viewHolder.itemView.top + checkIcon.intrinsicHeight
                            + textMargin
                )
                checkIcon.draw(c)
                super.onChildDraw(c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive)

            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO починить тулбар(после закрытия задачи он развернут)
        toolbar = view.findViewById(R.id.toolbar)
        recyclerView = view.findViewById(R.id.rv)
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(recyclerView)
        ItemTouchHelper(swipeToCheckCallback).attachToRecyclerView(recyclerView)
        tasksDoneTextView = view.findViewById(R.id.tasks_done_textview)
        visibilityButton = view.findViewById(R.id.change_visibility_button)
        visibilityButton.setOnClickListener {
            isVisible=!isVisible
            changeItemsVisibility(data.value!!)
        }
        fab = view.findViewById(R.id.floatingActionButton)
        val layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = tasksAdapter
        navController = view.findNavController()
        recyclerView.layoutManager = layoutManager
        fab.setOnClickListener {
            navController.navigate(R.id.action_todoListFragment_to_taskFragment)
        }
    }
    private fun changeItemsVisibility(arrayList: ArrayList<TodoItem>){
        tasksAdapter.isVisibilityChanges=true
        if(isVisible){
            visibilityButton.setImageResource(R.drawable.visible)
        }else{
            visibilityButton.setImageResource(R.drawable.invisible)
        }
        val data = arrayListOf<TodoItem>()
        var cou = 0
        for (i in arrayList){
            if(i.isCompleted){
                cou++
            }else{
                if(!isVisible){
                    data.add(i)
                }
            }
        }
        tasksDoneTextView.text=re.replace( tasksDoneTextView.text,"")+cou.toString()
        if(!tasksAdapter.isCheckedChange or !isVisible){
            tasksAdapter.tasks = if(isVisible) arrayList else data
            //TODO плохое обновление

            tasksAdapter.notifyDataSetChanged()

        }
        recyclerView.runWhenReady {
            tasksAdapter.isVisibilityChanges=false
        }
        tasksAdapter.isCheckedChange=false
    }
}