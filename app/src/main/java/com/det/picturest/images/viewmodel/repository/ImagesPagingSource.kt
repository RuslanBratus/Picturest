package com.det.picturest.images.viewmodel.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.det.picturest.images.model.Image
import com.det.picturest.images.viewmodel.repository.room.ImagesDao

class ImagesPagingSource(private val dao: ImagesDao):
    PagingSource<Int, Image>() {
    override fun getRefreshKey(state: PagingState<Int, Image>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Image> {
        val page = params.key ?: 0
        return try {
            val entities = dao.getPagedListImages(params.loadSize, page * params.loadSize)
            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}