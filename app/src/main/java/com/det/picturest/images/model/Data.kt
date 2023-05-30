package com.det.picturest.images.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "images_table", indices = [Index(value = ["custom_name"], unique = true)])
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "custom_name") var customName: String
)

sealed class Resource {
    data class Success(val T: Any): Resource()
    data class Error(val throwable: Throwable): Resource()
    data class Loading(val isLoading: Boolean): Resource()
}