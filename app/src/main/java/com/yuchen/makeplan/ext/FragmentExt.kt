package com.yuchen.makeplan.ext

import androidx.fragment.app.Fragment
import com.yuchen.makeplan.MakePlanApplication
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.MultiTask
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.factory.*

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(projectHistory : Array<Project>): GanttViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return GanttViewModelFactory(repository,projectHistory)
}

fun Fragment.getVmFactory(projectHistory : Array<Project>, taskPos : Int,colorList : List<String>): TaskViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return TaskViewModelFactory(repository,projectHistory,taskPos,colorList)
}

fun Fragment.getVmFactory(project: Project?): EditViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return EditViewModelFactory(repository,project)
}

fun Fragment.getVmFactory(project: MultiProject?): MultiEditViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return MultiEditViewModelFactory(repository,project)
}

fun Fragment.getVmFactory(project: MultiProject): MultiGanttViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return MultiGanttViewModelFactory(repository,project)
}

fun Fragment.getVmFactory(project: MultiProject, task: MultiTask?): MultiTaskViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    val application = requireNotNull(this.activity).application
    return MultiTaskViewModelFactory(repository,project,task,application)
}

fun Fragment.getVmFactory(pagerPos: Int): NotifyItemsViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return NotifyItemsViewModelFactory(repository,pagerPos)
}

fun Fragment.getVmFactory(project: MultiProject,membersPos: Int): MembersItemsViewModelFactory {
    val repository = (requireContext().applicationContext as MakePlanApplication).makePlanRepository
    return MembersItemsViewModelFactory(repository,project,membersPos)
}