package com.callhistory.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

public class RetrofitBuilder {
    companion object {
        fun create(baseUrl: String): ApiService {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(RemoveCharacterInterceptor())
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }

    class RemoveCharacterInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val path = request.url().toString()

            var string = path.replace("%3F", "?") // replace
            string = string.replace("%2F", "/") // replace

            val newRequest = request.newBuilder()
                .url(string)
                .build()

            return chain.proceed(newRequest)
        }

    }
}
