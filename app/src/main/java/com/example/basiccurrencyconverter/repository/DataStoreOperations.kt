package com.example.basiccurrencyconverter.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreOperations {
    suspend fun saveCurrencyPairs(currencyPairs: List<Map<String, String>>)
    fun retrieveCurrencyPairs(): Flow<List<Map<String, String>>>
    suspend fun retrieveFirstOpenState(): Boolean
    suspend fun saveFirstOpenState()
}