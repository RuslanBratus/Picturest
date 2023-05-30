package com.det.picturest.images.viewmodel.repository.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.det.picturest.images.model.Image


@Dao
interface ImagesDao {

    @Query("SELECT * FROM images_table ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getPagedListImages(limit: Int, offset: Int): List<Image>

    @Query("SELECT * FROM images_table WHERE custom_name LIKE :name ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getPagedListImages(name: String, limit: Int, offset: Int): List<Image>

    @Insert
    suspend fun saveImage(image: Image)

    @Delete
    suspend fun deleteImages(images: List<Image?>?)

}