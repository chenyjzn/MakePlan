package com.yuchen.makeplan.ext

import android.content.res.Resources
import android.util.Log
import android.widget.TextView
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.User
import kotlin.math.roundToInt

fun Int.toPx(): Int {
    return (Resources.getSystem().displayMetrics.density * this).roundToInt()
}

fun Float.toDp(): Float {
    return (this/Resources.getSystem().displayMetrics.density)
}

fun MutableList<*>.removeFrom(index:Int) {
    while (this.lastIndex > index ){
        removeAt(this.lastIndex)
    }
}

fun TextView.setWidthAndPos(){

}

fun List<User>.toUidStringList() : List<String>{
    return this.map {
        it.uid
    }
}