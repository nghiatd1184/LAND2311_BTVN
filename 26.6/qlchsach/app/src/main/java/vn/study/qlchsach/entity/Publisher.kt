package vn.study.qlchsach.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_publisher")
data class Publisher(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0, // sẽ bị sai ở trường hợp save thứ 2 khi @insert của tôi để onConflict
    // là NONE

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "address")
    val address: String,

//    @Ignore
//    var isSelected: Boolean = false
)