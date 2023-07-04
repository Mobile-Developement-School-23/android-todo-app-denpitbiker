package com.advancedsolutionsdevelopers.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity
@Serializable
data class TodoItem(
    @PrimaryKey val id: String,
    var text: String,
    @SerialName("importance") var priority: Priority,
    @SerialName("done") var isCompleted: Boolean,
    @SerialName("created_at") var creationDate: Long,
    @SerialName("changed_at") var lastEditDate: Long,
    @SerialName("last_updated_by") var lastUpdatedBy: String,
    @Transient var isDeleted: Boolean = false,
    @SerialName("deadline") var deadlineDate: Long? = null,
    var color: String? = null
)
fun blankTodoItem(): TodoItem {
    return TodoItem(
        "",
        "",
        Priority.low,
        false,
        0L,
        0L,
        ""
    )
}
