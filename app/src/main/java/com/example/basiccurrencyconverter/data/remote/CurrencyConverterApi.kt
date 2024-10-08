package com.example.basiccurrencyconverter.data.remote

import com.example.basiccurrencyconverter.domain.model.CurrencyRates
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface CurrencyConverterApi {

    @GET("v6/latest/USD")
    suspend fun getRates(): CurrencyRates
}