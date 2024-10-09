package com.example.basiccurrencyconverter.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.basiccurrencyconverter.util.Constants.CURRENCY_RATES
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = CURRENCY_RATES)
data class CurrencyRates(

    @PrimaryKey @SerializedName("time_last_update_utc") val time_last_update_utc: String,

    @SerializedName("result") val result: String,
    @SerializedName("provider") val provider: String,
    @SerializedName("documentation") val documentation: String,
    @SerializedName("terms_of_use") val terms_of_use: String,
    @SerializedName("time_last_update_unix") val time_last_update_unix: Int,
    @SerializedName("time_next_update_unix") val time_next_update_unix: Int,
    @SerializedName("time_next_update_utc") val time_next_update_utc: String,
    @SerializedName("time_eol_unix") val time_eol_unix: Int,
    @SerializedName("base_code") val base_code: String,
    @SerializedName("rates") val rates: Map<String, Double>
)
