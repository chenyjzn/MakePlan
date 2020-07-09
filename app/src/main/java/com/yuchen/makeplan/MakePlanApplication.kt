package com.yuchen.makeplan

import android.app.Application
import com.yuchen.makeplan.data.source.MakePlanRepository
import com.yuchen.makeplan.util.ServiceLocator
import kotlin.properties.Delegates

class MakePlanApplication : Application() {

    val makePlanRepository: MakePlanRepository
        get() = ServiceLocator.provideTasksRepository(this)

    companion object {
        var instance: MakePlanApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
