package com.example.basiccurrencyconverter.data.repository

import com.example.basiccurrencyconverter.data.remote.CurrencyConverterApi
import com.example.basiccurrencyconverter.domain.model.CurrencyRates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class Repository @Inject constructor(
    private val currencyConverterApi: CurrencyConverterApi
) {

//    suspend fun getRates(): Flow<CurrencyRates> {
//        return currencyConverterApi.getRates()
//    }

    fun getRates(): Flow<CurrencyRates> = flow {
        val response = currencyConverterApi.getRates()
        emit(response)
    }.flowOn(Dispatchers.IO)

}