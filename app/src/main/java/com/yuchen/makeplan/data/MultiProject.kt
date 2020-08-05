package com.yuchen.makeplan.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MultiProject(
    var name: String = "Project",
    var updateTime: Long = System.currentTimeMillis(),
    var members: MutableList<User> = mutableListOf(),
    var membersUid: MutableList<String> = mutableListOf(),
    var firebaseId: String = "",
    var completeRate: Int = 0,
    var sendUid: MutableList<String> = mutableListOf(),
    var receiveUid: MutableList<String> = mutableListOf()
) : Parcelable