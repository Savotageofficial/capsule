package com.example.noxnews

import retrofit2.Call
import retrofit2.http.GET

interface NewsCallable {

    @GET("/v2/top-headlines?country=us&category=general&apiKey=e145d0ed4d974df59b294a451d4d4e96&pageSize=60")
    fun getNews(): Call<News>


}