package com.yuchen.makeplan.ext

import android.content.res.Resources
import android.util.Log
import kotlin.math.roundToInt

fun Int.toPx(): Int {
    //Log.d("chenyjzn","${Resources.getSystem().displayMetrics.density }")
    return (Resources.getSystem().displayMetrics.density * this).roundToInt()
}