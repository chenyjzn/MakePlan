package com.yuchen.makeplan.ext

import android.app.Activity
import com.yuchen.makeplan.MakePlanApplication
import com.yuchen.makeplan.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as MakePlanApplication).makePlanRepository
    return ViewModelFactory(repository)
}
