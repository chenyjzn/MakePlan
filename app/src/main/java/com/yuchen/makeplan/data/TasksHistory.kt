package com.yuchen.makeplan.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TasksHistory (
    var tasksHistory: MutableList<List<Task>> = mutableListOf()
): Parcelable