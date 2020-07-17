package com.yuchen.makeplan.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var displayName: String = "",
    var email: String = "",
    var photoUrl: String = "",
    val uid: String = ""
) : Parcelable