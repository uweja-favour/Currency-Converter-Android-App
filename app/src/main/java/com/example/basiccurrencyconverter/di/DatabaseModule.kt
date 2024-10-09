package com.example.basiccurrencyconverter.di

import android.content.Context
import androidx.room.Room
import com.example.basiccurrencyconverter.data.local.AppDatabase
import com.example.basiccurrencyconverter.data.local.CurrencyRatesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }


    @Singleton
    @Provides
    fun provideCurrencyRatesDao(appDatabase: AppDatabase): CurrencyRatesDao {
        return appDatabase.currencyRatesDao()
    }


}