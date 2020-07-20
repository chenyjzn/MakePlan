package com.yuchen.makeplan.ext

import android.app.Application
import androidx.fragment.app.Fragment
import com.yuchen.makeplan.MakePlanApplication
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.edit.EditViewModel
import com.yuchen.makeplan.factory.*
import com.yuchen.makeplan.multitask.MultiTaskViewModel

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(isMultiProject : Boolean): ProjectsViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return ProjectsViewModelFactory(repository, isMultiProject)
}

fun Fragment.getVmFactory(projectHistory : Array<Project>,isMultiProject:Boolean): GanttViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return GanttViewModelFactory(repository,projectHistory,isMultiProject)
}

fun Fragment.getVmFactory(projectHistory : Array<Project>, taskPos : Int, colorList : List<String>,isMultiProject:Boolean): TaskViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return TaskViewModelFactory(repository,projectHistory,taskPos,colorList,isMultiProject)
}

fun Fragment.getVmFactory(project: Project?,isMultiProject : Boolean): EditViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return EditViewModelFactory(repository,project,isMultiProject)
}

fun Fragment.getVmFactory(project: Project): MultiGanttViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return MultiGanttViewModelFactory(repository,project)
}

fun Fragment.getVmFactory(project: Project, task: Task?): MultiTaskViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    val application = requireNotNull(this.activity).application
    return MultiTaskViewModelFactory(repository,project,task,application)
}