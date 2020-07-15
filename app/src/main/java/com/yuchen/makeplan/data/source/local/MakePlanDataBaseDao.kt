package com.yuchen.makeplan.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yuchen.makeplan.data.Project

@Dao
interface MakePlanDataBaseDao {
    @Insert
    fun insertProject(inCartProduct: Project)

    @Update
    fun updateProject(inCartProduct: Project)

    @Delete
    fun removeProject(project: Project)

    @Query("SELECT * FROM project_list ORDER BY project_update_time DESC")
    fun getAllProjects():LiveData<List<Project>>

    @Query("SELECT * FROM project_list WHERE id = :id")
    fun searchProject(id: Long): Project?
}