package com.det.picturest.images.viewmodel.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.det.picturest.images.model.Image
import javax.inject.Singleton


@Singleton
@Database(entities = [Image::class], version = 1)
abstract class ImagesDatabase: RoomDatabase() {
    abstract fun imagesDao(): ImagesDao
}