package com.yuchen.makeplan.data

import android.os.Parcelable
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.data.source.local.MakePlanConverters
import kotlinx.android.parcel.Parcelize
import kotlin.math.roundToInt

@Parcelize
data class MultiProject (
    var name: String = "Project",
    var updateTime: Long = System.currentTimeMillis(),
    var members: MutableList<User> = mutableListOf(),
    var membersUid: MutableList<String> = mutableListOf(),
    var firebaseId: String = "",
    var completeRate : Int = 0
) : Parcelable