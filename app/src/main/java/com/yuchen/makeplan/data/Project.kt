package com.yuchen.makeplan.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.data.source.local.MakePlanConverters
import kotlinx.android.parcel.Parcelize
import kotlin.math.roundToInt

@Entity(tableName = "project_list")
@TypeConverters(MakePlanConverters::class)
@Parcelize
data class Project (
    @PrimaryKey(autoGenerate = true)
    var id:Long=0L,
    @ColumnInfo(name = "project_start_time")
    var startTimeMillis : Long = System.currentTimeMillis(),
    @ColumnInfo(name = "project_end_time")
    var endTimeMillis : Long = startTimeMillis + 7 * DAY_MILLIS,
    @ColumnInfo(name = "project_name")
    val name: String = "Project",
    @ColumnInfo(name = "project_task_list")
    var taskList: MutableList<Task> = mutableListOf()
) : Parcelable
{
    val completeRate : Int
        get() {
            var fb = 0.0f
            for (i in taskList){
                fb += (i.startTimeMillis-i.endTimeMillis)*completeRate
            }
            return fb.roundToInt()
        }

    fun newRefProject(): Project{
        val newProject = this.copy(taskList = mutableListOf())
        this.taskList.map {task->
            newProject.taskList.add(task.copy(toDoList = mutableListOf()))
            task.toDoList.map {toDo->
                newProject.taskList.last().toDoList.add(toDo)
            }
        }
        return newProject
    }
}