package com.tuanha.language.di

import com.tuanha.language.BuildConfig
import com.tuanha.language.data.api.AppApi
import com.tuanha.language.data.api.CloneApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

@JvmField
val apiModule = module {

    single {
        OkHttpClient
            .Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE))
//            .addInterceptor(DecryptInterceptor())
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("http://45.77.206.34:3000/")
            .addConverterFactory(JacksonConverterFactory.create())
            .client(get())
            .build()
    }

    single {

        (get() as Retrofit).create(AppApi::class.java)
    }

    single {

        (get() as Retrofit).create(CloneApi::class.java)
    }
}