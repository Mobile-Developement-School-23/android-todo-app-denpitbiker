package com.advancedsolutionsdevelopers.todoapp.utils

import com.advancedsolutionsdevelopers.todoapp.data.models.TodoItem

class ItemsMerger {
    fun merge(serverData: List<TodoItem>, localData: List<TodoItem>): MergerOutput<TodoItem> {
        val res = MergerOutput<TodoItem>()
        val added = mutableSetOf<String>()
        for (i in serverData) {
            val item = localData.find { it.id == i.id }
            if (item == null) {
                res.newItemsFromServ.add(i)
                added.add(i.id)
            } else {
                ifExistsInDB(res,added,i,item)
            }
        }
        for (i in localData) {
            if (i.id !in added && !i.isDeleted) {
                res.newItemsFromDB.add(i)
            }
        }
        return res
    }

    private fun ifExistsInDB(
        res: MergerOutput<TodoItem>,
        added: MutableSet<String>,
        servItem: TodoItem,
        localItem: TodoItem
    ) {
        if (servItem.lastEditDate > localItem.lastEditDate) {
            res.needUpdate.add(servItem)
            added.add(servItem.id)
        } else if (servItem.lastEditDate < localItem.lastEditDate) {
            if (localItem.isDeleted) {
                res.deletedOnDevice.add(localItem)//deletedOnDevice(When was no internet)
            } else {
                res.needUpdate.add(localItem)
            }
            added.add(localItem.id)
        } else if (localItem.isDeleted) {
            res.deletedOnDevice.add(localItem)//deletedOnDevice(When was no internet)
            added.add(localItem.id)
        } else {
            added.add(localItem.id)
        }
    }
}