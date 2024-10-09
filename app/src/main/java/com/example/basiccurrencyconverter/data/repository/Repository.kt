package com.example.basiccurrencyconverter.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.example.basiccurrencyconverter.data.local.CurrencyRatesDao
import com.example.basiccurrencyconverter.data.remote.CurrencyConverterApi
import com.example.basiccurrencyconverter.domain.model.CurrencyRates
import com.example.basiccurrencyconverter.repository.DataStoreOperations
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(
    private val currencyConverterApi: CurrencyConverterApi,
    private val currencyRatesDao: CurrencyRatesDao,
    private val dataStoreOperations: DataStoreOperations
) {

//    suspend fun getRates(): CurrencyRates {
//        return currencyConverterApi.getRates()
//    }

//    fun getRates(): Flow<CurrencyRates> = flow {
//        val response = currencyConverterApi.getRates()
//        emit(response)
//    }.flowOn(Dispatchers.IO)

    suspend fun fetchRatesFromInternet(context: Context): CurrencyRates? {
        return try {
            if (isNetworkAvailable(context)) {
                currencyConverterApi.getRates()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d("DEBUG", "Exception from fetchRatesFromInternet Repository function: $e")
            null
        }
    }

    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork?.let { network ->
                connectivityManager.getNetworkCapabilities(network)
            }
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    suspend fun getRatesFromAppDatabase(): CurrencyRates? {
        return currencyRatesDao.getRatesFromAppDatabase()
    }

    suspend fun deleteAllCurrencyRates() {
        currencyRatesDao.deleteAllCurrencyRates()
    }

    suspend fun saveCurrencyRatesToAppDatabase(currencyRates: CurrencyRates) {
        currencyRatesDao.saveCurrencyRates(currencyRates = currencyRates)
    }

    suspend fun saveCurrencyPairsToAppDataStore(currencyPairs: List<Map<String, Double>>) {
        dataStoreOperations.saveCurrencyPairs(currencyPairs)
    }

    fun retrieveCurrencyPairsFromAppDataStore(): Flow<List<Map<String, Double>>> {
        return dataStoreOperations.retrieveCurrencyPairs()
    }

    suspend fun saveFirstOpenState() {
        Log.d("MY LOG", "REPO HAS BEEN CALLED")
        dataStoreOperations.saveFirstOpenState()
        Log.d("MY LOG", "REPO HAS BEEN CALLED 2" )

    }

    suspend fun retrieveFirstOpenState(): Boolean {
        return dataStoreOperations.retrieveFirstOpenState()
    }
}