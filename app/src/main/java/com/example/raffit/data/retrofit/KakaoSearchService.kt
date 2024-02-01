package com.example.raffit.data.retrofit

import com.example.raffit.data.model.ApiResponse
import com.example.raffit.data.model.ImageDocument
import com.example.raffit.data.model.VclipDocument
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoSearchService {
    @GET("v2/search/image") //카카오 이미지
    suspend fun searchImage(//인터셉터 okhttp로 키 넣어줌
        @Query("query") query: String, //검색을 원하는 질의어
        @Query("sort") sort: String? = "recency", // 정렬 방식 accuracy(정확도순) 또는 recency(최신순), 기본 값 accuracy
        @Query("page") page: Int, // 결과 페이지 번호, 1~50 사이의 값, 기본 값 1
        @Query("size") size: Int? = 50 // 한 페이지에 보여질 문서 수, 1~80 사이의 값, 기본 값 80
    ) : ApiResponse<ImageDocument>

    @GET("v2/search/vclip")
    suspend fun searchVclip(
        @Query("query") query: String,
        @Query("sort") sort: String? = "recency",
        @Query("page") page: Int,
        @Query("size") size: Int? = 30
    ) : ApiResponse<VclipDocument>
}