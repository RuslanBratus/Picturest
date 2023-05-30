package com.det.picturest.images.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.det.picturest.images.model.Image
import com.det.picturest.images.viewmodel.repository.ImagesPagingSource
import com.det.picturest.images.viewmodel.repository.ImagesSearchingPagingSource
import com.det.picturest.images.viewmodel.repository.room.ImagesDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImagesViewModel @Inject constructor(
    private val imagesDatabase: ImagesDatabase
): ViewModel() {

    fun getImagesPaging(searchQuery: String): Flow<PagingData<Image>> {
        return if (searchQuery.isEmpty()) {
            Pager(
                PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false,
                    initialLoadSize = 20
                ),
            ) {
                ImagesPagingSource(imagesDatabase.imagesDao())
            }.flow.cachedIn(viewModelScope)
        }
        else {
            Pager(
                PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false,
                    initialLoadSize = 20
                ),
            ) {
                ImagesSearchingPagingSource(
                    imagesDatabase.imagesDao(),
                    searchQuery = "%$searchQuery%"
                )
            }.flow.cachedIn(viewModelScope)
        }
    }

    fun deleteImages(imagesToDelete: List<Image>) {
        viewModelScope.launch {
            imagesDatabase.imagesDao().deleteImages(imagesToDelete)
        }
    }

}