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

    @Query("SELECT * FROM project_list ORDER BY id DESC")
    fun getAllProjects():LiveData<List<Project>>

//
//    @Query("DELETE FROM cart_product_table")
//    fun clear()
//
//    @Query("SELECT * FROM cart_product_table ORDER BY id DESC")
//    fun getAllCartProducts():LiveData<List<InCartProduct>>
//
//    @Query("SELECT * FROM cart_product_table")
//    fun getAllCartProductsCursor():List<InCartProduct>
//
//    @Query("SELECT * FROM cart_product_table ORDER BY id DESC LIMIT 1")
//    fun getLastCartProduct():InCartProduct?
}