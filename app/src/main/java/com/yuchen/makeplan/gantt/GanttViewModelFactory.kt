package com.yuchen.makeplan.gantt

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GanttViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GanttViewModel::class.java)) {
            return GanttViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}