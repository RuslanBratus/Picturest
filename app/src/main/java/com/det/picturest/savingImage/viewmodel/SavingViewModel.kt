package com.det.picturest.savingImage.viewmodel

import android.database.SQLException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.det.picturest.images.model.Image
import com.det.picturest.images.model.Resource
import com.det.picturest.images.viewmodel.repository.room.ImagesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SavingViewModel @Inject constructor(
    private val imagesDatabase: ImagesDatabase
): ViewModel() {
    private val _savingProcess = MutableLiveData<Resource>()
    val savingProcess get() = _savingProcess

    fun saveImage(image: Image) {
        _savingProcess.postValue(Resource.Loading(true))
            viewModelScope.launch {
                try {
                    withContext(Dispatchers.Default) {
                        imagesDatabase.imagesDao().saveImage(image)
                    }
                    _savingProcess.postValue(Resource.Success(true))
                }catch (sqliteThrowable: SQLException) {
                    _savingProcess.postValue(Resource.Error(sqliteThrowable))
                } catch (throwable: Throwable) {
                    _savingProcess.postValue(Resource.Error(throwable))
                }
        }
    }

}