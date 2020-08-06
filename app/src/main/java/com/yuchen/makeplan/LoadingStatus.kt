package com.yuchen.makeplan

enum class LoadingStatus {
    LOADING,
    ERROR,
    DONE
}

//sealed class LoadingStatus {
//    data class ERROR(val message: String) : LoadingStatus()
//    object LOADING : LoadingStatus()
//    object DONE : LoadingStatus()
//}