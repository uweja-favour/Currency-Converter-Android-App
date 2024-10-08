package com.example.basiccurrencyconverter.di

import com.example.basiccurrencyconverter.data.remote.CurrencyConverterApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true // Ignore unknown keys in JSON
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofitInstance(httpClient: OkHttpClient): Retrofit {
        val contentType = MediaType.get("application/json")
        return Retrofit.Builder()
            .baseUrl("https://open.er-api.com/")
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }


    @Provides
    @Singleton
    fun providesCurrencyConverterAPi(retrofit: Retrofit): CurrencyConverterApi {
        return retrofit.create(CurrencyConverterApi::class.java)
    }

}