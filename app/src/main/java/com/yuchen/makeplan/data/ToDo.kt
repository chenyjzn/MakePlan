package com.yuchen.makeplan.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ToDo(
    val name : String = "ToDo",
    var isFinish : Boolean = false
) : Parcelable