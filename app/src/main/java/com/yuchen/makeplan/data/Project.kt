package com.yuchen.makeplan.data

data class Project (
    val startTimeMillis : Long = 0L,
    val endTimeMillis : Long = 0L,
    val name: String = "Project",
    var taskList: List<Task> = listOf()
)