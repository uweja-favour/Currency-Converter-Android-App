package com.example.basiccurrencyconverter.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.basiccurrencyconverter.data.type_converter.Converters
import com.example.basiccurrencyconverter.domain.model.CurrencyRates


@Database(version = 1, entities = [CurrencyRates::class], exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyRatesDao(): CurrencyRatesDao
}