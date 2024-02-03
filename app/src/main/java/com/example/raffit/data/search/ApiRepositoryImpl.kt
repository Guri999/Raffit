package com.example.raffit.data.search

import android.util.Log
import com.example.raffit.data.model.ApiResponse
import com.example.raffit.data.model.ImageDocument
import com.example.raffit.data.model.SearchModel
import com.example.raffit.data.model.VclipDocument
import com.example.raffit.data.retrofit.KakaoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ApiRepositoryImpl : ApiRepository {
    private var searchList: MutableList<SearchModel> = mutableListOf()

    private val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH-mm-ss")

    private suspend fun searchImage(
        query: String,
        sort: String,
        page: Int,
        size: Int?
    ): ApiResponse<ImageDocument> {
        return KakaoApi.kakaoNetwork.searchImage(
            query = query,
            sort = sort,
            page = page,
            size = size
        )
    }

    private suspend fun searchVclip(
        query: String,
        sort: String,
        page: Int,
        size: Int?
    ): ApiResponse<VclipDocument> {
        return KakaoApi.kakaoNetwork.searchVclip(
            query = query,
            sort = sort,
            page = page,
            size = size
        )
    }

    override suspend fun searchData(
        query: String,
        sort: String,
        page: Int,
        type: String
    ): MutableList<SearchModel> {
        if (page == 1) searchList.clear()
        when (type) {
            "total" -> searchList.addAll(loadApiData(query, sort, page))
            "image" -> searchList.addAll(loadImageData(query, sort, page))
            "videos" -> searchList.addAll(loadVideoData(query, sort, page))
        }

        return searchList
    }

    private suspend fun loadImageData(
        query: String,
        sort: String,
        page: Int
    ): MutableList<SearchModel> {
        return withContext(Dispatchers.IO) {
            val imageResultsDeferred = async {
                searchImage(query, sort, page, 80)
                    .documents?.map { convertImageSearchModel(it) }
            }
            val imageResults = imageResultsDeferred.await() ?: listOf()

            if (sort != "accuracy") {
                imageResults.sortedByDescending { LocalDateTime.parse(it.date, formatter) }
                    .toMutableList()
            } else imageResults.toMutableList()
        }
    }

    private suspend fun loadVideoData(
        query: String,
        sort: String,
        page: Int
    ): MutableList<SearchModel> {
        return withContext(Dispatchers.IO) {
            val vclipResultsDeferred = async {
                searchVclip(query, sort, page, 30)
                    .documents?.map { convertVideoSearchModel(it) }
            }
            val vclipResults = vclipResultsDeferred.await() ?: listOf()
            if (sort != "accuracy") {
                vclipResults.sortedByDescending { LocalDateTime.parse(it.date, formatter) }
                    .toMutableList()
            } else {
                vclipResults.toMutableList()
            }
        }
    }

    private suspend fun loadApiData(
        query: String,
        sort: String,
        page: Int
    ): MutableList<SearchModel> {
        val start = System.currentTimeMillis()
        return withContext(Dispatchers.IO) {
            val imageResultsDeferred = async {
                searchImage(query, sort, page, 50)
                    .documents?.map { convertImageSearchModel(it) }
            }
            val vclipResultsDeferred = async {
                searchVclip(query, sort, page, 30)
                    .documents?.map { convertVideoSearchModel(it) }
            }
            val imageResults = imageResultsDeferred.await() ?: listOf()
            val vclipResults = vclipResultsDeferred.await() ?: listOf()
            val combinedResults = imageResults + vclipResults
            val end = System.currentTimeMillis()
            Log.d("test_loadApiData", "Time: ${end - start}")

            if (sort != "accuracy") {
                combinedResults.sortedByDescending { LocalDateTime.parse(it.date, formatter) }
                    .toMutableList()
            } else {
                combinedResults.toMutableList()
            }
        }
    }

    private fun convertImageSearchModel(document: ImageDocument): SearchModel {
        return SearchModel(
            thumnail = document.imageUrl.toString(),
            title = document.displaySitename.toString(),
            siteName = document.collection.toString(),
            postType = "Image",
            date = dateTimeFormatter(document.datetime.toString()),
            docUrl = document.docUrl.toString()
        )
    }

    private fun convertVideoSearchModel(document: VclipDocument): SearchModel {
        return SearchModel(
            thumnail = document.thumbnail.toString(),
            title = document.title.toString(),
            siteName = document.author.toString(),
            postType = "Video",
            date = dateTimeFormatter(document.datetime.toString()),
            docUrl = document.url.toString()
        )
    }

    private fun dateTimeFormatter(date: String): String {
        val originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val targetFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH-mm-ss")
        val dateTime = LocalDateTime.parse(date, originalFormatter)
        return dateTime.format(targetFormatter)
    }
}