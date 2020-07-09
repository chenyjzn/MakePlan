package com.yuchen.makeplan.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yuchen.makeplan.data.Project

@Dao
interface MakePlanDataBaseDao {
    @Insert
    fun insert(inCartProduct: Project)

    @Update
    fun update(inCartProduct: Project)

    @Query("SELECT * FROM project_list ORDER BY id DESC")
    fun getAllProjects():LiveData<List<Project>>

//    @Query("SELECT * FROM cart_product_table WHERE cart_product_id = :productId AND cart_product_color_code = :productColorCode AND cart_product_size = :productSize ")
//    fun get(productId: Long,productColorCode: String,productSize:String): Project?
//
//    @Delete
//    fun delete(inCartProduct: InCartProduct)
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