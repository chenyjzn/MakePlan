package com.yuchen.makeplan.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.notify.NotifyItemsViewModel

class NotifyItemsViewModelFactory constructor(
    private val makePlanRepository: MakePlanRepository,
    private val notifyPos: Int
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(NotifyItemsViewModel::class.java) ->
                    NotifyItemsViewModel(makePlanRepository,notifyPos)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}