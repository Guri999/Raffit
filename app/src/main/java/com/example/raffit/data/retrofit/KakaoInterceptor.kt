package com.example.raffit.data.retrofit

import com.example.raffit.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class KakaoInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val auth = "KakaoAK ${BuildConfig.REST_API_KEY}"

        builder.addHeader("Authorization", auth)

        return chain.proceed(builder.build())
    }
}