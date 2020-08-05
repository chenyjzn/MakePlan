package com.yuchen.makeplan.ext

import android.content.res.Resources
import kotlin.math.roundToInt

fun Int.toPx(): Int {
    return (Resources.getSystem().displayMetrics.density * this).roundToInt()
}

fun Float.toDp(): Float {
    return (this / Resources.getSystem().displayMetrics.density)
}

fun MutableList<*>.removeFrom(index: Int) {
    while (this.lastIndex > index) {
        removeAt(this.lastIndex)
    }
}
