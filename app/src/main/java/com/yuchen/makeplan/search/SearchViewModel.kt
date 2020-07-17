package com.yuchen.makeplan.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.source.MakePlanRepository

class SearchViewModel (private val repository: MakePlanRepository) : ViewModel(){
    val projects: LiveData<List<Project>> = repository.getAllMultiProjectsFromFirebase()

}