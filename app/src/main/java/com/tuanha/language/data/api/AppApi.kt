package com.tuanha.language.data.api

import com.tuanha.language.entities.Subtitle
import retrofit2.http.GET
import retrofit2.http.Headers

interface AppApi {

    @Headers(value = ["enableDecrypt:false"])
    @GET("https://raw.githubusercontent.com/hoanganhtuan95ptit/4Language/main/uEATpbQ9md4.json")
    suspend fun fetchSubtitles(): List<Subtitle>
}
