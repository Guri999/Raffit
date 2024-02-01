package com.example.raffit.data.retrofit

import com.example.raffit.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object KakaoApi {

    private const val KAKAO_BASE_URL = "https://dapi.kakao.com/"


    private fun createOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }
        else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }


        return OkHttpClient.Builder()
            .addNetworkInterceptor(interceptor)
            .addInterceptor(KakaoInterceptor())
            .build()
    }
    private val gson = GsonBuilder()
        .setDateFormat("yy-MM-dd HH-mm-ss")
        .create()

    private val kakaoRetrofit =
        Retrofit.Builder().baseUrl(KAKAO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
//            .addConverterFactory(GsonConverterFactory.)
            .client(
            createOkHttpClient() // Gson팩토리에서 데이트 컨퍼팅 가능 addConverterFactory 최소 논란의 법칙
        ).build()

    val kakaoNetwork: KakaoSearchService = kakaoRetrofit.create(KakaoSearchService::class.java)
}