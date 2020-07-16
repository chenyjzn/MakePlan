package com.yuchen.makeplan.data

data class Team(
    val name : String = "",
    val firebaseId : String = "",
    val createTime : Long = 0L,
    val leader : User = User()
)