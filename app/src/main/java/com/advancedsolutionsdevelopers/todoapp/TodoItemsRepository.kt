package com.advancedsolutionsdevelopers.todoapp

import com.advancedsolutionsdevelopers.todoapp.recyclerView.TodoItem
import java.time.LocalDate
import java.util.ArrayList
import java.util.Calendar
import kotlin.random.Random

class TodoItemsRepository {
    //Да, я в курсе про многопоточность!!! Просто без реального внешнего хранилища городить тут
    //всякое не очень хотелось. А 30 элементов не вызовут фриз системы:)
    fun getTasks(): ArrayList<TodoItem> {
        val arr = arrayListOf<TodoItem>()
        val random = Random(Calendar.getInstance().time.time)
        for (i in 0..random.nextInt(11, 30)) {
            arr.add(
                TodoItem(
                    i.toLong(),
                    "Privet".repeat(random.nextInt(1, 21)),
                    random.nextInt(0, 3).toByte(),
                    random.nextBoolean(),
                    LocalDate.of(
                        2023,
                        random.nextInt(1, 13),
                        random.nextInt(1, 29)
                    ),
                    if (random.nextBoolean())
                        null
                    else
                        LocalDate.of(
                            2023,
                            random.nextInt(1, 13),
                            random.nextInt(1, 29)
                        ),null
                )
            )
        }
        arr.add(
            TodoItem(
                777L, "Итальянская пицца, как же прекрасно тобою поживится\n" +
                        "Это то, что я люблю поутру и не только\n" +
                        "Это только я и не только я", 2, false,
                LocalDate.of(2023,6,12), LocalDate.of(2023,6,12), null
            )
        )
        return arr
    }
}