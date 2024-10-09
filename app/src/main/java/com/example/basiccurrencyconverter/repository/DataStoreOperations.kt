package com.example.basiccurrencyconverter.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreOperations {
    suspend fun saveCurrencyPairs(currencyPairs: List<Map<String, Double>>)
    fun retrieveCurrencyPairs(): Flow<List<Map<String, Double>>>
    suspend fun retrieveFirstOpenState(): Boolean
    suspend fun saveFirstOpenState()
}