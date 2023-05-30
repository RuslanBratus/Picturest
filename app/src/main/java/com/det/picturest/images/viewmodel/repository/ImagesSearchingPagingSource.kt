package com.det.picturest.images.viewmodel.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.det.picturest.images.model.Image
import com.det.picturest.images.viewmodel.repository.room.ImagesDao

class ImagesSearchingPagingSource(private val dao: ImagesDao, private val searchQuery: String):
    PagingSource<Int, Image>() {
    override fun getRefreshKey(state: PagingState<Int, Image>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            Log.i("klalo", "anchorPosition = $anchorPosition")
            val anchorPage = state.closestPageToPosition(anchorPosition)
            Log.i("klalo", "anchorPage = $anchorPage")
            Log.i("klalo", "anchorPagePrev = ${anchorPage?.prevKey}")
            Log.i("klalo", "anchorPageNext = ${anchorPage?.nextKey}")
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Image> {
        val page = params.key ?: 0
        Log.i("klalo", "page = $page")
        return try {
            val entities = dao.getPagedListImages(searchQuery, params.loadSize, page * params.loadSize)
            Log.i("klalo", "entities = $entities")
            //@TODO REMOVE
            val tmp = LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )
            Log.i("klalo", "loadResult = $tmp")
            tmp
        } catch (e: Exception) {
            Log.i("klalo", "ERROR = $e")
            LoadResult.Error(e)
        }
    }
}