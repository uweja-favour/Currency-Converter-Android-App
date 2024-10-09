package com.example.basiccurrencyconverter.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basiccurrencyconverter.data.repository.Repository
import com.example.basiccurrencyconverter.domain.model.CurrencyRates
import com.example.basiccurrencyconverter.util.Constants.NGN
import com.example.basiccurrencyconverter.util.Constants.USD
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext private val context: Context
): ViewModel() {

    private val _rates = MutableStateFlow<CurrencyRates?>(null)

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet

    private val _baseCurrency = MutableStateFlow<Map<String, Double>?>(null)
    val baseCurrency = _baseCurrency

    private val _targetCurrency = MutableStateFlow<Map<String, Double>?>(null)
    val targetCurrency = _targetCurrency

    private val _baseCurrencySelected = MutableStateFlow(false)
    val baseCurrencySelected = _baseCurrencySelected

    private val _targetCurrencySelected = MutableStateFlow(false)
    val targetCurrencySelected = _targetCurrencySelected

    private val _lastUpdated = MutableStateFlow("")
    val lastUpdated = _lastUpdated

    var baseCurrencyInput by mutableStateOf("")

    var targetCurrencyInput by mutableStateOf("")

    private var firstOpenState by mutableStateOf(false)



    init {
        checkFirstOpenState() // check whether or not the app has run
        initializeData()
    }

    private fun checkFirstOpenState() {
        Log.d("MY LOG", "CHECK FIRST OPEN STATE 1")
        viewModelScope.launch(Dispatchers.IO) {
            firstOpenState = repository.retrieveFirstOpenState()
            Log.d("MY LOG", "CHECK FIRST OPEN STATE 2")
            Log.d("MY LOG", "CHECK FIRST OPEN STATE value of firstOpenState is $firstOpenState")
        }
    }



    fun updateBaseCurrencyInput(number: String) {
        baseCurrencyInput = number
        Log.d("MY NUMBERS", "baseCurrencyInput is $baseCurrencyInput")
        if (baseCurrencyInput.isNotBlank()) {
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
        Log.d("MY NUMBERS", "targetCurrencyInput is $targetCurrencyInput")
        if (targetCurrencyInput.isNotBlank()) {
            updateTargetCurrencyValue()
            performConversionForTargetCurrency()
        } else {
            Log.d("MY NUMBERS", "HELLO THERE ELSE!!!")
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

    private fun finishUpdateOfBaseCurrency(baseCurrency: Map<String, Number>? = _baseCurrency.value, selected: Boolean = true) {
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

    private fun finishUpdateOfTargetCurrency(targetCurrency: Map<String, Number>? = _targetCurrency.value, selected: Boolean = true) {
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
                val baseCurrencyValueInUSD = _baseCurrency.value!![USD]
                val targetCurrencyName = _targetCurrency.value?.keys?.first()
                val targetCurrencyConversionRate = _rates.value?.rates!![targetCurrencyName]

                if (baseCurrencyValueInUSD != 0.0) {
                    val solution = targetCurrencyConversionRate?.let {
                        baseCurrencyValueInUSD?.times(
                            it
                        )
                    }
                    targetCurrencyInput = solution.toString()
                    finishUpdateOfTargetCurrency()
                    targetCurrencyInput = ""
                } else {
                    targetCurrencyInput = "0.0"
                    finishUpdateOfTargetCurrency()
                    targetCurrencyInput = ""
                }
            }  else {
                // convert new baseCurrency to dollars
                // do this based on the exchange rate
                val newBaseCurrencyName = _baseCurrency.value?.keys?.first()
                val newBaseCurrencyValue = _baseCurrency.value?.values?.first()

                // grab the conversion rate of the new base currency
                val newBaseCurrencyDollarConversionRate: Double = _rates.value?.rates?.get(newBaseCurrencyName.toString())!!

                // first, convert the new base currency into dollars
                val newBaseCurrencyInDollars = newBaseCurrencyValue?.div(
                    newBaseCurrencyDollarConversionRate
                )

                // convert the dollars into the target currency value
                val targetCurrencyName = _targetCurrency.value?.keys?.first()
                val targetCurrencyDollarConversionRate = _rates.value?.rates!![targetCurrencyName.toString()]!!

                val solution = newBaseCurrencyInDollars?.times(
                    targetCurrencyDollarConversionRate
                )
                targetCurrencyInput = solution.toString()
                finishUpdateOfTargetCurrency()
                targetCurrencyInput = ""


                // 1600 NGN = 1 dollars
                // 3 NGN = x dollars

                // 1600x = 3
                // x = 3 / 16000

                // x would be in dollars



                //

            }
        }
    }

    private fun performConversionForTargetCurrency() {
        // if dollar is the baseCurrency
        viewModelScope.launch(Dispatchers.IO) {
            if (_baseCurrency.value?.keys?.first() == USD) {
                val targetCurrencyValue = _targetCurrency.value?.values?.first()
                val targetCurrencyName = _targetCurrency.value?.keys?.first()
                val targetCurrencyConversionRate = _rates.value?.rates!![targetCurrencyName]

                if (targetCurrencyValue != 0.0) {
                    val solution = (targetCurrencyConversionRate?.let { targetCurrencyValue?.div(it) })
                    baseCurrencyInput = solution.toString()
                    finishUpdateOfBaseCurrency()
                    baseCurrencyInput = ""
                } else {
                    baseCurrencyInput = "0.0"
                    finishUpdateOfBaseCurrency()
                    baseCurrencyInput = ""
                }
            } else {
                // convert new baseCurrency to dollars
                // do this based on the exchange rate
//                val newBaseCurrencyName = _baseCurrency.value?.keys?.first()
//                val newBaseCurrencyValue = _baseCurrency.value?.values?.first()
                val newTargetCurrencyName = _targetCurrency.value?.keys?.first()
                val newTargetCurrencyValue = _targetCurrency.value?.values?.first()

                // grab the conversion rate of the new base currency
                val newTargetCurrencyConversionRate: Double = _rates.value?.rates?.get(newTargetCurrencyName.toString())!!

                // first, convert the new target currency into dollars
                val newTargetCurrencyInDollars = newTargetCurrencyValue?.div(
                    newTargetCurrencyConversionRate
                )

                // convert the dollars into the base currency value
                val baseCurrencyName = _baseCurrency.value?.keys?.first()
                val baseCurrencyDollarConversionRate = _rates.value?.rates!![baseCurrencyName.toString()]!!

                val solution = newTargetCurrencyInDollars?.times(
                    baseCurrencyDollarConversionRate
                )
                baseCurrencyInput = solution.toString()
                finishUpdateOfBaseCurrency()
                baseCurrencyInput = ""

                // 1600 NGN = 1 dollars
                // 3 NGN = x dollars

                // 1600x = 3
                // x = 3 / 16000

                // x would be in dollars



                //

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
    private var targetCurrencyValue: Double = 0.0



    private fun initializeData() {
        var currencyRatesFromAppDatabase: CurrencyRates? = null
        Log.d("MY LOG", "START")
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MY LOG", "START 2")
            currencyRatesFromAppDatabase = repository.getRatesFromAppDatabase()
            Log.d("MY LOG", "START 3")
            if (currencyRatesFromAppDatabase == null) {
                // If database is empty, fetch data from internet
                Log.d("MY LOG", "IF STATEMENT RAN")
                fetchNewCurrencyRatesFromInternet()
            } else {
                // Retrieve data from database
                Log.d("MY LOG", "ELSE STATEMENT RAN")
                updateUI(newCurrencyRates = currencyRatesFromAppDatabase!!)
            }
        }
    }

    fun fetchNewCurrencyRatesFromInternet() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MY LOG", "Fetching new currency rates... BE AWARE")
            val newCurrencyRates = repository.fetchRatesFromInternet(context)
            if (newCurrencyRates != null) {
                updateUI(newCurrencyRates = newCurrencyRates)
                deleteAllCurrencyRatesFromAppDatabase()
                storeNewCurrencyRatesToAppDatabase(currencyRates = newCurrencyRates)
            } else {
                Toast.makeText(context, "Connect to the Internet", Toast.LENGTH_LONG).show()
                Log.d("MY LOG", "set error has been called... BE AWARE")
                setError()
            }
        }
    }

    private fun setError() {
        Log.d("MY LOG", "SET ERROR FUNCTION CALLED")
        baseCurrency.value = mapOf("null" to 0.0)
        targetCurrency.value = mapOf("null" to 0.0)
        _lastUpdated.value = _rates.value?.time_last_update_utc ?: "No Network"
    }

    private fun getCurrencyPairsFromAppDataStore() {
        Log.d("MY LOG", "updateCurrencyPairsFromDataStore function has been called")
        viewModelScope.launch(Dispatchers.IO) {
            val currencyPairs = repository.retrieveCurrencyPairsFromAppDataStore().first()
            Log.d("MY LOG", "updateCurrencyPairsFromDataStore function has been called 2")
            if (currencyPairs.isNotEmpty() && currencyPairs != null) {
                Log.d("MY LOG", "updateCurrencyPairsFromDataStore function has been called 3")
                val baseCurrency = currencyPairs.first()
                val targetCurrency = currencyPairs.last()

                _baseCurrency.value = mapOf(baseCurrency.keys.first() to baseCurrency.values.first())
                _targetCurrency.value = mapOf(targetCurrency.keys.first() to targetCurrency.values.first())
                _lastUpdated.value = _rates.value?.time_last_update_utc ?: "No Internet"
                Log.d("MY LOG", "updateCurrencyPairsFromDataStore function has successfully updated base and target currencies")
            } else {
                Log.d("MY LOG", "updateCurrencyPairsFromDataStore function has been called, SER ERROR ABOUT TO BE CALLED")
                setError()
            }
        }
    }

    private fun updateUI(newCurrencyRates: CurrencyRates) {
        viewModelScope.launch {
            _rates.value = newCurrencyRates
            Log.d("MY LOG", "Rates fetched successfully: $newCurrencyRates")
            if (!firstOpenState) {
                Log.d("MY LOG", "value of firstOpenState is $firstOpenState")
                var baseCurrencyKey = ""
                _rates.value?.rates?.keys?.forEach {
                    if (it == USD) {
                        baseCurrencyKey = it
                        baseCurrencyValue = _rates.value?.rates!![it]!!
                    }
                }
                var targetCurrencyKey = ""
                _rates.value?.rates?.keys?.forEach {
                    if (it == NGN) {
                        targetCurrencyKey = it
                        targetCurrencyValue = _rates.value?.rates!![it]!!
                    }
                }
                _baseCurrency.value = mapOf(baseCurrencyKey to baseCurrencyValue)
                _targetCurrency.value = mapOf(targetCurrencyKey to targetCurrencyValue)
                _lastUpdated.value = _rates.value?.time_last_update_utc ?: ""
                repository.saveFirstOpenState()
                saveCurrencyPairsToAppDataStore()
            } else {
                // app has been open before so...
                getCurrencyPairsFromAppDataStore()
            }
        }
    }

    private fun storeNewCurrencyRatesToAppDatabase(currencyRates: CurrencyRates) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveCurrencyRatesToAppDatabase(currencyRates)
            Log.d("MY LOG", "7")
        }
    }

    private fun deleteAllCurrencyRatesFromAppDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllCurrencyRates()
        }
    }

    fun handleNewTargetCurrencyName(newTargetCurrency: String) {
        viewModelScope.launch {
            if (newTargetCurrency.isNotEmpty()) {
                Log.d("MY LOG", "value of newTargetCurrency is $newTargetCurrency")
                val currency = newTargetCurrency.split(" ").last()
                Log.d("MY LOG", "value of currency is $currency")
                val previousTargetCurrencyValue = _targetCurrency.value?.values?.first()
                _targetCurrency.value = mapOf(currency to (previousTargetCurrencyValue ?: 0.0))
                performConversionForTargetCurrency()
                saveCurrencyPairsToAppDataStore()
            }
        }
    }

    fun handleNewBaseCurrencyName(newBaseCurrency: String) {
        viewModelScope.launch {
            if (newBaseCurrency.isNotEmpty()) {
                Log.d("MY LOG", "value of newTargetCurrency is $newBaseCurrency")
                val currency = newBaseCurrency.split(" ").last()
                val previousBaseCurrencyValue = _targetCurrency.value?.values?.first()
                _baseCurrency.value = mapOf(currency to (previousBaseCurrencyValue ?: 0.0))
                performConversionForBaseCurrency()
                saveCurrencyPairsToAppDataStore()
            }
        }
    }

    private fun saveCurrencyPairsToAppDataStore() {
        Log.d("MY LOG", "SAVED TO DATA STORE 1")
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MY LOG", "SAVED TO DATA STORE 2")
            if (_baseCurrency.value != null && _targetCurrency.value != null) {
                Log.d("MY LOG", "SAVED TO DATA STORE 3")
                repository.saveCurrencyPairsToAppDataStore(
                    listOf(
                        _baseCurrency.value!!,
                        _targetCurrency.value!!
                    )
                )
                Log.d("MY LOG", "SAVED TO DATA STORE COMPLETE")

            } else {
                Log.d("MY LOG", "SAVED TO DATA STORE SAYS THERE IS AN ERROR FOR SOME REASON")
                Log.d("MY LOG", "_baseCurrency.value is ${_baseCurrency.value}")
                Log.d("MY LOG", "_targetCurrency.value is ${_targetCurrency.value}")
            }
        }
    }


    private val _swapConversion = MutableStateFlow(false)
    private val swapConversion = _swapConversion


//    private fun getRates(): Flow<CurrencyRates> {
//        return repository.getRates().also {
//            Log.d("MY LOG", "Rates fetched successfully: $it")
//        }
//
//    }

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