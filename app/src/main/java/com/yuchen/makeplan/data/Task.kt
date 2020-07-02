package com.yuchen.makeplan.data

data class Task(
    val startTimeMillis : Long = 0L,
    val endTimeMillis : Long = 0L,
    val name: String = "Task"
)