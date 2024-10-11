package com.example.basiccurrencyconverter.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.basiccurrencyconverter.repository.DataStoreOperations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


val Context.datastore: DataStore<Preferences> by preferencesDataStore("currency_converter_preferences")
class DataStoreOperationsImpl(private val context: Context) : DataStoreOperations {

    private val dataStore = context.datastore
    private object PreferencesKeys {
        val CURRENCY_PAIRS = stringPreferencesKey("currency_pairs")
        val FIRST_OPEN_STATE = booleanPreferencesKey("first_open_state")
    }

    override suspend fun saveCurrencyPairs(currencyPairs: List<Map<String, Double>>) {
        val jsonList = Json.encodeToString(currencyPairs)
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY_PAIRS] = jsonList
        }
    }

    override fun retrieveCurrencyPairs(): Flow<List<Map<String, Double>>> {
       return dataStore.data
           .catch {
               if (it is IOException) {
                   emit(emptyPreferences())
               } else {
                   throw it
               }
           }

           .map { preferences ->
               // Retrieve the JSON string and convert it back to a list of maps
               preferences[PreferencesKeys.CURRENCY_PAIRS]?.let {
                   Json.decodeFromString<List<Map<String, Double>>>(it)
               }!!
           }
    }

    override suspend fun saveFirstOpenState() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_OPEN_STATE] = false
        }
    }

    override suspend fun retrieveFirstOpenState(): Boolean {
        return dataStore.data
            .catch {
                if (it is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map {
               it[PreferencesKeys.FIRST_OPEN_STATE] ?: true
            }.first()
    }

}