package com.det.picturest.images.viewmodel.injection

import android.app.Application
import androidx.room.Room
import com.det.picturest.images.viewmodel.repository.room.ImagesDao
import com.det.picturest.images.viewmodel.repository.room.ImagesDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(myApplication: Application): ImagesDatabase {
        synchronized(ImagesDatabase::class.java) {
            return Room.databaseBuilder(
                myApplication.applicationContext,
                ImagesDatabase::class.java, "images_database"
            ).build()
        }
    }

    @Provides
    @Singleton
    fun provideFavouriteImagesDao(database: ImagesDatabase): ImagesDao = database.imagesDao()
}