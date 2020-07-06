package com.yuchen.makeplan.data

data class Project (
    var startTimeMillis : Long = 0L,
    var endTimeMillis : Long = 0L,
    val name: String = "Project",
    var taskList: List<Task> = listOf()
)