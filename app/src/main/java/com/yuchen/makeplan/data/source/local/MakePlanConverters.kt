package com.yuchen.makeplan.data.source.local

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.data.ToDo

class MakePlanConverters {
    @TypeConverter
    fun convertTaskListToJson(listTask: MutableList<Task>): String? {
        listTask?.let {
            return Moshi.Builder().build().adapter<MutableList<Task>>(MutableList::class.java).toJson(listTask)
        }
        return null
    }

    @TypeConverter
    fun convertJsonToTaskList(json: String?): MutableList<Task>? {
        json?.let {
            val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)
            val adapter: JsonAdapter<MutableList<Task>> = Moshi.Builder().build().adapter(type)
            return adapter.fromJson(it)
        }
        return null
    }

    @TypeConverter
    fun convertToDoListToJson(listToDo: MutableList<ToDo>): String? {
        listToDo?.let {
            return Moshi.Builder().build().adapter<MutableList<ToDo>>(MutableList::class.java).toJson(listToDo)
        }
        return null
    }

    @TypeConverter
    fun convertJsonToToDoList(json: String?): MutableList<ToDo>? {
        json?.let {
            val type = Types.newParameterizedType(MutableList::class.java, ToDo::class.java)
            val adapter: JsonAdapter<MutableList<ToDo>> = Moshi.Builder().build().adapter(type)
            return adapter.fromJson(it)
        }
        return null
    }
}