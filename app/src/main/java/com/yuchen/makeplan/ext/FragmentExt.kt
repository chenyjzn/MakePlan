package com.yuchen.makeplan.ext

import androidx.fragment.app.Fragment
import com.yuchen.makeplan.MakePlanApplication
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.factory.GanttViewModelFactory
import com.yuchen.makeplan.factory.TaskViewModelFactory
import com.yuchen.makeplan.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(projectHistory : Array<Project>,projectHistoryPos : Int): GanttViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return GanttViewModelFactory(repository,projectHistory,projectHistoryPos)
}

fun Fragment.getVmFactory(projectHistory : Array<Project>,projectHistoryPos : Int,colorList : List<String>): TaskViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return TaskViewModelFactory(repository,projectHistory,projectHistoryPos,colorList)
}