package com.example.projekatfaza23.data.api

import com.example.projekatfaza23.data.auth.UserManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
       val token = UserManager.currentUser.value?.idToken
       val request = if (token != null) {
           chain.request().newBuilder()
               .addHeader("Authorization", "Bearer $token")
               .build()
       } else {
           chain.request()
       }
        return chain.proceed(request)
    }
}