package com.example.basiccurrencyconverter.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.io.Serial

@Serializable
data class Rates(
    @SerializedName("rates") val rates: Map<String, Double>
)
