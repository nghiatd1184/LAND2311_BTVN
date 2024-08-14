package com.nghiatd.quanlycuahangroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nghiatd.quanlycuahangroom.Const
import com.nghiatd.quanlycuahangroom.entity.Product

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(product: Product)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(products: List<Product>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(product: Product)

    @Delete
    fun delete(product: Product)

    @Query("SELECT * FROM ${Const.TBProduct.TABLE_NAME}")
    fun getAll(): List<Product>

    @Query("SELECT * FROM ${Const.TBProduct.TABLE_NAME} WHERE ${Const.TBProduct.COL_ID} = :id")
    fun getProductById(id: String): Product
}