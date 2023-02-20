package com.example.skytag3.base

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BaseRetrofit {
    private val okHttpClient = OkHttpClient()

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constans.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

}