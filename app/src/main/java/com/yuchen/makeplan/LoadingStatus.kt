package com.yuchen.makeplan

sealed class LoadingStatus {
    data class ERROR(val message: String) : LoadingStatus()
    object LOADING : LoadingStatus()
    object DONE : LoadingStatus()
}