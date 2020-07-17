package com.yuchen.makeplan.ext

import androidx.fragment.app.Fragment
import com.yuchen.makeplan.MakePlanApplication
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.edit.EditViewModel
import com.yuchen.makeplan.factory.*

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(isMultiProject : Boolean): ProjectsViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return ProjectsViewModelFactory(repository, isMultiProject)
}

fun Fragment.getVmFactory(projectHistory : Array<Project>): GanttViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return GanttViewModelFactory(repository,projectHistory)
}

fun Fragment.getVmFactory(projectHistory : Array<Project>, taskPos : Int, colorList : List<String>): TaskViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return TaskViewModelFactory(repository,projectHistory,taskPos,colorList)
}

fun Fragment.getVmFactory(project: Project?,isMultiProject : Boolean): EditViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return EditViewModelFactory(repository,project,isMultiProject)
}