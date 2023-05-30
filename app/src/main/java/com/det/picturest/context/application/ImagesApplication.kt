package com.det.picturest.context.application

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import com.det.picturest.context.application.injection.AppModule
import com.det.picturest.images.view.ImagesFragment
import com.det.picturest.images.viewmodel.injection.DatabaseModule
import com.det.picturest.savingImage.view.SavingImageFragment
import com.det.picturest.utils.Utils
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DatabaseModule::class])
interface AppComponent {
    fun inject(imagesFragment: ImagesFragment)
    fun inject(savingImageFragment: SavingImageFragment)
}

class ImagesApplication: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        getImagesDirectoryPath()
    }

    private fun getImagesDirectoryPath() {
        val directoryAbsolutePath: String? = getSharedPreferences(packageName, Context.MODE_PRIVATE)
            .getString(Utils.imagesDirectorySharedPrefKey, null)
        if (directoryAbsolutePath == null) {
            val cw = ContextWrapper(applicationContext)
            val directory = cw.getDir(Utils.imagesDirectoryName, Context.MODE_PRIVATE)
            Utils.imagesDirectoryPath = directory.absolutePath
            getSharedPreferences(packageName, Context.MODE_PRIVATE)
                .edit()
                .putString(Utils.imagesDirectorySharedPrefKey, directory.absolutePath)
                .apply()
        }
        else {
            Utils.imagesDirectoryPath = directoryAbsolutePath
        }

    }
}