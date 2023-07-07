package com.advancedsolutionsdevelopers.todoapp.utils

data class MergerOutput<T>(
    val needUpdate: ArrayList<T> = arrayListOf(),
    val newItemsFromServ: ArrayList<T> = arrayListOf(),
    val newItemsFromDB: ArrayList<T> = arrayListOf(),
    val deletedOnDevice: ArrayList<T> = arrayListOf()
)