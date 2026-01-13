package com.example.projekatfaza23.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val okHttpClient = provideOkHttpClient()
    private val retrofitGoogle = Retrofit.Builder()
        .baseUrl("https://people.api.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val googlePeopleApi : GooglePeopleApi = retrofitGoogle.create(GooglePeopleApi::class.java)
}