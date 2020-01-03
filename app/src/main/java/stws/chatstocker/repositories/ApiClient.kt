package com.kartikonlinewholeseller.apisrepositories

import android.content.Context
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object {
        private var retrofitAuth: Retrofit? = null
        private var retrofit: Retrofit? = null
        private var BASE_URL: String = "https://chat-stocker.firebaseio.com/"
        private val SIZE_OF_CACHE = (10 * 1024 * 1024).toLong() // 10 MB

        fun getRetrofitWithoutAuth(context: Context): Retrofit? {
            var cache: Cache? = null
            cache = Cache(File(context.cacheDir, "http"), SIZE_OF_CACHE)
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            //        TokenInterceptor tokenInterceptor=new TokenInterceptor(context);
            val client = OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(cache)
                    //                .addInterceptor(tokenInterceptor)
                    .addInterceptor(interceptor).build()


            // client.networkInterceptors().add(mCacheControlInterceptor);
            if (retrofit == null)
                retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
                        .client(client)
                        .build()
            return retrofit

        }
    }


}