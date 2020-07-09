package com.yuchen.makeplan.ext

import android.content.res.Resources
import android.util.Log
import com.yuchen.makeplan.data.Project
import kotlin.math.roundToInt

fun Int.toPx(): Int {
    return (Resources.getSystem().displayMetrics.density * this).roundToInt()
}

fun Float.toDp(): Float {
    return (this/Resources.getSystem().displayMetrics.density)
}