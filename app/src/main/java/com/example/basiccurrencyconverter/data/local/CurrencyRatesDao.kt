package com.example.basiccurrencyconverter.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.basiccurrencyconverter.domain.model.CurrencyRates
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyRatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrencyRates(currencyRates: CurrencyRates)





    @Query("SELECT * FROM currency_rates")
    suspend fun getAllRates(): List<CurrencyRates>?

    suspend fun getRatesFromAppDatabase(): CurrencyRates? {
        val rates = getAllRates()
        return if (rates.isNullOrEmpty()) {
            null // Database is empty
        } else {
            rates.first() // Return the first item if not empty
        }
    }
//
//    @Query("SELECT * FROM currency_rates ORDER BY time_last_update_utc LIMIT 1")
//    fun getCurrencyRates(): Flow<CurrencyRates>?

    @Query("DELETE FROM currency_rates")
    suspend fun deleteAllCurrencyRates()

}