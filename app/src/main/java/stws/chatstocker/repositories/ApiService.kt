package com.kartikonlinewholeseller.apisrepositories


import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @GET("Users.json")
    fun listuser(): Observable<JsonObject>




}