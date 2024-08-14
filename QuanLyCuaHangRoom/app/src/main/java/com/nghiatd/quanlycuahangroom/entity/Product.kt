package com.nghiatd.quanlycuahangroom.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nghiatd.quanlycuahangroom.Const
import com.nghiatd.quanlycuahangroom.R

@Entity(tableName = Const.TBProduct.TABLE_NAME)
data class Product(
    @ColumnInfo(name = Const.TBProduct.COL_ID)
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = Const.TBProduct.COL_NAME)
    val name: String,

    @ColumnInfo(name = Const.TBProduct.COL_INVENTORY_QUANTITY)
    val inventoryQuantity: Int,

    @ColumnInfo(name = Const.TBProduct.COL_PRICE)
    val price: Double,

    @ColumnInfo(name = Const.TBProduct.COL_RES_ID)
    val resID: Int = R.drawable.img_product
)
