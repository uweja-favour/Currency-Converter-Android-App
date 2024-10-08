package com.example.basiccurrencyconverter.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basiccurrencyconverter.data.repository.Repository
import com.example.basiccurrencyconverter.domain.model.CurrencyRates
import com.example.basiccurrencyconverter.util.Constants.NGN
import com.example.basiccurrencyconverter.util.Constants.USD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _rates = MutableStateFlow<CurrencyRates?>(null)

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet

    private val _baseCurrency = MutableStateFlow<Map<String, Number>?>(null)
    val baseCurrency = _baseCurrency

    private val _targetCurrency = MutableStateFlow<Map<String, Number>?>(null)
    val targetCurrency = _targetCurrency

    private val _baseCurrencySelected = MutableStateFlow(false)
    val baseCurrencySelected = _baseCurrencySelected

    private val _targetCurrencySelected = MutableStateFlow(false)
    val targetCurrencySelected = _targetCurrencySelected

    private val _lastUpdated = MutableStateFlow("")
    val lastUpdated = _lastUpdated

    var baseCurrencyInput by mutableStateOf("")

    var targetCurrencyInput by mutableStateOf("")

    private val _currencyClicked = MutableStateFlow("")
    val currencyClicked = _currencyClicked

    fun updateCurrencyClicked(currencyClicked: String) {
        _currencyClicked.value = currencyClicked
        Log.d("MY LOG", "just updated! value is ${_currencyClicked.value}")
    }

    init {
        initializeData()
    }

    fun updateBaseCurrencyInput(number: String) {
       baseCurrencyInput = number
        if (baseCurrencyInput.isNotEmpty()) {
            updateBaseCurrencyValue()
            performConversionForBaseCurrency()

        } else {
            baseCurrencyInput = "0"
            updateBaseCurrencyValue()
            performConversionForBaseCurrency()
            baseCurrencyInput = ""
        }
    }

    fun updateTargetCurrencyInput(number: String) {
        targetCurrencyInput = number
        if (targetCurrencyInput.isNotEmpty()) {
            updateTargetCurrencyValue()
            performConversionForTargetCurrency()
        } else {
            targetCurrencyInput = "0"
            updateTargetCurrencyValue()
            performConversionForTargetCurrency()
            targetCurrencyInput = ""
        }
    }

    private fun updateBaseCurrencyValue() {
        val baseCurrency = _baseCurrency.value
        val selected = _baseCurrencySelected.value
       finishUpdateOfBaseCurrency(baseCurrency = baseCurrency, selected = selected)
    }

    private fun finishUpdateOfBaseCurrency(baseCurrency: Map<String, Number>?, selected: Boolean) {
        if (baseCurrency != null && selected) {
            val key = baseCurrency.keys.firstOrNull()
            if (key != null) {
                _baseCurrency.value = mapOf(key to baseCurrencyInput.toDouble())
            }
        }
    }

    private fun updateTargetCurrencyValue() {
        val targetCurrency = _targetCurrency.value
        val selected = _targetCurrencySelected.value
        finishUpdateOfTargetCurrency(targetCurrency = targetCurrency, selected = selected)
    }

    private fun finishUpdateOfTargetCurrency(targetCurrency: Map<String, Number>?, selected: Boolean) {
        if (targetCurrency != null && selected) {
            val key = targetCurrency.keys.firstOrNull()
            if (key != null) {
                _targetCurrency.value = mapOf(key to targetCurrencyInput.toDouble())
            }
        }
    }

    private fun performConversionForBaseCurrency() {
        // if dollar is the baseCurrency
        viewModelScope.launch(Dispatchers.IO) {
            if (_baseCurrency.value?.keys?.first() == USD) {
                val digitOfBaseCurrencyInUSD = _baseCurrency.value!![USD]
                var otherCurrencyConversionRate = 0.0
                val otherCurrency = _targetCurrency.value
                _rates.value?.rates?.keys?.forEach {
                    if (it == _targetCurrency.value?.keys?.first()) {
                        otherCurrencyConversionRate = _rates.value?.rates!![it]!!
                    }
                }
                if (otherCurrencyConversionRate != 0.0 && digitOfBaseCurrencyInUSD?.toDouble() != 0.0) {
                    val solution = digitOfBaseCurrencyInUSD?.toDouble()?.times(otherCurrencyConversionRate)
                    targetCurrencyInput = solution.toString()
                    finishUpdateOfTargetCurrency(targetCurrency = otherCurrency, selected = true)
                    targetCurrencyInput = ""
                } else if (digitOfBaseCurrencyInUSD?.toDouble() == 0.0) {
                    targetCurrencyInput = "0.0"
                    finishUpdateOfTargetCurrency(targetCurrency = otherCurrency, selected = true)
                    targetCurrencyInput = ""
                }
            }
        }

    }

    private fun performConversionForTargetCurrency() {
        // if dollar is the baseCurrency
        viewModelScope.launch(Dispatchers.IO) {
            if (_baseCurrency.value?.keys?.first() == USD) {
                val digitOfOtherCurrency = _targetCurrency.value?.values?.first()
                var otherCurrencyConversionRate = 0.0
                val baseCurrency = _baseCurrency.value
                _rates.value?.rates?.keys?.forEach {
                    if (it == _targetCurrency.value?.keys?.first()) {
                        otherCurrencyConversionRate = _rates.value?.rates!![it]!!
                    }
                }
                if (otherCurrencyConversionRate != 0.0 && digitOfOtherCurrency?.toDouble() != 0.0) {
                    val solution = (digitOfOtherCurrency?.toDouble()?.div(otherCurrencyConversionRate))
                    baseCurrencyInput = solution.toString()
                    finishUpdateOfBaseCurrency(baseCurrency = baseCurrency, selected = true)
                    baseCurrencyInput = ""
                } else if (digitOfOtherCurrency?.toDouble() == 0.0) {
                    baseCurrencyInput = "0.0"
                    finishUpdateOfBaseCurrency(baseCurrency = baseCurrency, selected = true)
                    baseCurrencyInput = ""
                }
            }
        }

    }

    fun handleNewTargetCurrency(newTargetCurrency: String) {
        viewModelScope.launch {
            if (newTargetCurrency.isNotEmpty()) {
                Log.d("MY LOG", "value of newTargetCurrency is $newTargetCurrency")
                val currency = newTargetCurrency.split(" ").last()
                Log.d("MY LOG", "value of currency is $currency")
                val previousTargetCurrencyValue = _targetCurrency.value?.values?.first()
                _targetCurrency.value = mapOf(currency to (previousTargetCurrencyValue ?: 0.0))
                performConversionForTargetCurrency()
            }
        }
    }

    fun setBaseCurrencySelectedToTrue() {
        _baseCurrencySelected.value = true
    }

    fun setTargetCurrencySelectedToTrue() {
        _targetCurrencySelected.value = true
    }

    fun setBaseCurrencySelectedToFalse() {
        _baseCurrencySelected.value = false
    }

    fun setTargetCurrencySelectedToFalse() {
        _targetCurrencySelected.value = false
    }

    private var baseCurrencyValue: Double = 0.0
    private var otherCurrencyValue: Double = 0.0



    fun initializeData() {

        viewModelScope.launch(Dispatchers.IO) {
            getRates().conflate().distinctUntilChanged().collect {
                _rates.value = it
            }
            var baseCurrencyKey = ""
            _rates.value?.rates?.keys?.forEach {
                if (it == _rates.value?.base_code) {
                    baseCurrencyKey = it
                    baseCurrencyValue = _rates.value?.rates!![it]!!
                }
            }
            var otherCurrencyKey = ""
            _rates.value?.rates?.keys?.forEach {
                if (it == NGN) {
                    otherCurrencyKey = it
                    otherCurrencyValue = _rates.value?.rates!![it]!!
                }
            }

            _baseCurrency.value = mapOf(baseCurrencyKey to baseCurrencyValue)
            _targetCurrency.value = mapOf(otherCurrencyKey to otherCurrencyValue)
            _lastUpdated.value = _rates.value?.time_last_update_utc ?: ""
        }
    }

    private val _swapConversion = MutableStateFlow(false)
    private val swapConversion = _swapConversion


    private fun getRates(): Flow<CurrencyRates> {
        return repository.getRates().also {
            Log.d("MY LOG", "Rates fetched successfully: $it")
        }
    }

    fun toggleShowBottomSheetOff() {
        _showBottomSheet.value = false
    }

    fun toggleShowBottomSheetOn() {
        _showBottomSheet.value = true
    }

    fun toggleSwapConversion() {
        _swapConversion.value = !swapConversion.value
    }

}