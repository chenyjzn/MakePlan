package com.yuchen.makeplan.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yuchen.makeplan.data.Project


@Database(entities = [Project::class], version = 3, exportSchema = false)
abstract class MakePlanDataBase : RoomDatabase() {
    abstract val makePlanDataBaseDao: MakePlanDataBaseDao

    companion object {
        @Volatile
        private var INSTANCE: MakePlanDataBase? = null
        fun getInstance(context: Context): MakePlanDataBase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MakePlanDataBase::class.java,
                        "make_plan_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}