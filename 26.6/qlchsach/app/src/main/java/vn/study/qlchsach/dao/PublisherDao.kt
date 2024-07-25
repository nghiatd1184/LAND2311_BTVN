package vn.study.qlchsach.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import vn.study.qlchsach.entity.Publisher

@Dao
interface PublisherDao {
    @Insert
    fun insertItem(publisher: Publisher)

    @Query("SELECT * FROM tbl_publisher")
    fun getAll(): List<Publisher>

    @Query("SELECT name FROM tbl_publisher")
    fun getAllReturnName(): List<String>
}