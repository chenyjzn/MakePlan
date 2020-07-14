package com.yuchen.makeplan.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val name: String? = null,
    val email: String? = null,
    val imageUri: Uri? = null,
    val fireBaseId: String? = null,
    val listProject : List<String>?= null,
    val listGroup : List<String>? = null
) : Parcelable