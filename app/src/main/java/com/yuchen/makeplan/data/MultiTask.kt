package com.yuchen.makeplan.data

import android.os.Parcelable
import com.yuchen.makeplan.DAY_MILLIS
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MultiTask(
    var startTimeMillis : Long = System.currentTimeMillis(),
    var endTimeMillis : Long = System.currentTimeMillis() + 7 * DAY_MILLIS,
    var name: String = "Task",
    var completeRate : Int = 0,
    var color : Int = 0,
    var taskOwners : List<User> = listOf(),
    var toDoList : MutableList<ToDo> = mutableListOf(),
    var firebaseId: String = ""
) : Parcelable