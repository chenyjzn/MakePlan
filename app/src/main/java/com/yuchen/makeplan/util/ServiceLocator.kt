package com.yuchen.makeplan.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.yuchen.makeplan.data.source.DefaultMakePlanRepository
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.data.source.local.MakePlanLocalDataSource
import com.yuchen.makeplan.data.source.remote.MakePlanRemoteDataSource

object ServiceLocator {

    @Volatile
    var makePlanRepository: MakePlanRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): MakePlanRepository {
        synchronized(this) {
            return makePlanRepository ?: createMakePlanRepository(context)
        }
    }

    private fun createMakePlanRepository(context: Context): MakePlanRepository {
        return DefaultMakePlanRepository(MakePlanRemoteDataSource, createLocalDataSource(context))
    }

    private fun createLocalDataSource(context: Context): MakePlanLocalDataSource {
        return MakePlanLocalDataSource(context)
    }

}