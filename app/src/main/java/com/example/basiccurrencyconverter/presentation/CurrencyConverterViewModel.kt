package com.example.basiccurrencyconverter.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basiccurrencyconverter.data.repository.Repository
import com.example.basiccurrencyconverter.domain.model.CurrencyRates
import com.example.basiccurrencyconverter.util.Constants.NGN
import com.example.basiccurrencyconverter.util.Constants.REFRESH
import com.example.basiccurrencyconverter.util.Constants.USD
import com.example.basiccurrencyconverter.util.Constants.allCurrencies
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext private val context: Context
): ViewModel() {

    // Encapsulate state with private MutableStateFlow
    private val _rates = MutableStateFlow<CurrencyRates?>(null)


    // Currencies state
    private val _baseCurrency = MutableStateFlow<Map<String, String>?>(null)
    val baseCurrency: StateFlow<Map<String, String>?> get() = _baseCurrency

    private val _targetCurrency = MutableStateFlow<Map<String, String>?>(null)
    val targetCurrency: StateFlow<Map<String, String>?> get() = _targetCurrency


    private val _currencyRatesLastUpdated = MutableStateFlow("")
    val currencyRatesLastUpdated: StateFlow<String> get() = _currencyRatesLastUpdated

    // Inputs and values
    var baseCurrencyInput = MutableStateFlow("0")
    var targetCurrencyInput = MutableStateFlow("0")

    // Track if currencies were selected
    private val _wasBaseCurrencyExpandedToChangeItsName = MutableStateFlow(false)
    val wasBaseCurrencyExpandedToChangeItsName: StateFlow<Boolean> get() = _wasBaseCurrencyExpandedToChangeItsName

    private val _baseCurrencySelected = MutableStateFlow(false)
    val baseCurrencySelected get() = _baseCurrencySelected

    private val _targetCurrencySelected = MutableStateFlow(false)
    val targetCurrencySelected get() = _targetCurrencySelected

    // Debounce job for search input
    private var searchJob: Job? = null
    var searchInputText = MutableStateFlow("")

    // Currency list
    private val _listBuilder = MutableStateFlow(emptyList<String>())
    val listBuilder get() = _listBuilder

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating


    fun initializeDataIfNeeded() {
        viewModelScope.launch {
            initializeData()
            handleBaseCurrencySelected()
        }
    }


    fun updateIfBaseCurrencyWasClicked(boolean: Boolean) {
        _wasBaseCurrencyExpandedToChangeItsName.value = boolean
    }


    /**
     * Updates the search input with a debouncing mechanism to avoid redundant list generation.
     * Removed redundant coroutine job cancellations, now using structured concurrency.
     */
    fun updateSearchInputText(text: String) {
        searchInputText.value = text
        searchJob?.cancel() // Cancel the previous job if any
        searchJob = viewModelScope.launch {
            delay(300) // Debounce delay
            if (text.isNotBlank()) {
                fetchSearchList()
            } else {
                _listBuilder.value = emptyList() // Clear list if input is blank
            }
        }
    }

    /**
     * Fetches the filtered list of currencies based on the search input text.
     */
    private suspend fun fetchSearchList() {
        withContext(Dispatchers.Default) { // Ensure this runs on the default dispatcher
            _listBuilder.value = allCurrencies.filter {
                it.contains(searchInputText.value, ignoreCase = true)
            }
        }
    }

    /**
     * Simplified function for updating base currency input and triggering conversion.
     * Optimized by removing redundant state changes and avoiding launching multiple coroutines.
     */
    fun updateBaseCurrencyInput(input: String) {
        baseCurrencyInput.value = input.ifBlank { "0" }
        instantiateBaseCurrency(
            name = _baseCurrency.value?.keys?.first() ?: "LINE 121",
            value =  baseCurrencyInput.value
        )
//        if (input.isNotBlank()) {
            viewModelScope.launch { // Main Dispatcher
                // update base currency with new value

                performConversionForBaseCurrency()
            }
//        }
    }

    /**
     * Simplified function for updating target currency input and triggering conversion.
     * Avoided multiple state updates and redundant coroutines.
     */
    fun updateTargetCurrencyInput(input: String) {
        targetCurrencyInput.value = input.ifBlank { "0" }
        instantiateTargetCurrency(
            name = _targetCurrency.value?.keys?.first() ?: "LINE 134",
            value = targetCurrencyInput.value
        )
//        if (input.isNotBlank()) {
            viewModelScope.launch {
                // update target currency with new value
                performConversionForTargetCurrency()
            }
//        }
    }



    /**
     * Combined the logic for currency conversion into one function to avoid redundancy.
     */
    private suspend fun performConversionForBaseCurrency() {
        withContext(Dispatchers.Default) {
            val baseCurrency = _baseCurrency.value?.keys?.firstOrNull()
            val baseValue = _baseCurrency.value?.values?.first()

            if (baseCurrency == USD) {
                convertBaseToTarget(baseValue!!.toDouble())
            } else {
                convertNonUSDToTarget(baseValue!!.toDouble())
            }
        }
    }

    /**
     * Converts the base currency (in USD) to the target currency.
     */
    private suspend fun convertBaseToTarget(baseValue: Double) {
        val targetCurrencyName = _targetCurrency.value?.keys?.first()
        val conversionRate = _rates.value?.rates?.get(targetCurrencyName)

        val targetValue = conversionRate?.times(baseValue) ?: 0.0
        targetCurrencyInput.value = targetValue.toString()

        if (targetCurrencyInput.value == "0.0") {
            targetCurrencyInput.value = "0"
        }
        // update _targetCurrency value
        instantiateTargetCurrency(
            name = targetCurrencyName ?: "THE ERROR HERE!",
            value =  targetCurrencyInput.value
        )
        if (_targetCurrency.value?.keys?.first() != "THE ERROR HERE!") {
            withContext(Dispatchers.IO) {
                saveCurrencyPairsToAppDataStore() // update dataStore pair
            }
        }
    }

    /**
     * Converts non-USD base currencies to the target currency by first converting to USD.
     */
    private suspend fun convertNonUSDToTarget(baseValue: Double) {
        // Convert baseCurrency to USD first, then convert that to target currency
        val baseCurrencyName = _baseCurrency.value?.keys?.first()
        val conversionRate = _rates.value?.rates?.get(baseCurrencyName)

        val baseInUSD = conversionRate?.let { baseValue / it } ?: 0.0
        convertBaseToTarget(baseInUSD)
    }

    /**
     * Combined and simplified function for target currency conversion logic.
     */
    private suspend fun performConversionForTargetCurrency() {
        withContext(Dispatchers.Default) {
            val targetCurrency = _targetCurrency.value?.keys?.firstOrNull()
            val targetValue = _targetCurrency.value?.values?.first()

            if (targetCurrency == USD) {
                convertTargetToBase(targetValue!!)
            } else {
                convertNonUSDTargetToBase(targetValue!!)
            }
        }
    }

    private suspend fun convertTargetToBase(targetValue: String) {
        // Convert targetCurrency from USD to baseCurrency
        val baseCurrencyName = _baseCurrency.value?.keys?.first()
        val conversionRate = _rates.value?.rates?.get(baseCurrencyName)

        // Multiply targetValue (USD) by the conversion rate to get the base currency value
        val baseValue = conversionRate?.let { targetValue.toDouble() * it } ?: 0.0
        baseCurrencyInput.value = baseValue.toString()

        if (baseCurrencyInput.value == "0.0") {
            baseCurrencyInput.value = "0"
        }

        // update _baseCurrency value
        instantiateBaseCurrency(
            name = baseCurrencyName ?: "THE ERROR HERE!",
            value = baseCurrencyInput.value
        )
        if (_baseCurrency.value?.keys?.first() != "THE ERROR HERE!") {
            withContext(Dispatchers.IO) {
                saveCurrencyPairsToAppDataStore() // save pair to dataStore
            }
        }
    }


    private suspend fun convertNonUSDTargetToBase(targetValue: String) {
        // Convert targetCurrency to USD first, before converting to baseCurrency
        val targetCurrencyName = _targetCurrency.value?.keys?.first()
        val conversionRate = _rates.value?.rates?.get(targetCurrencyName)

        val targetInUSD = conversionRate?.let { targetValue.toDouble() / it } ?: 0.0
        convertTargetToBase(targetInUSD.toString())
    }


    /**
     * Initializes the ViewModel's data. Checks if the app is opened for the first time.
     */
    private suspend fun initializeData() {
        withContext(Dispatchers.IO) {
            val cachedRates = repository.getRatesFromAppDatabase()
            _rates.value = cachedRates

            if (_rates.value == null) {
                fetchNewCurrencyRatesFromAPI()
            } else {
                // if database data is valid
                checkFirstOpenStateAndUpdateUI()
            }
        }
    }

    /**
     * Fetches new currency rates from the internet and updates the local database.
     */
    private suspend fun fetchNewCurrencyRatesFromAPI() {
        _isUpdating.value = true
        try {
            val newRates = repository.fetchRatesFromInternet(context)
            _rates.value = newRates

            if (_rates.value != null) {
                repository.saveCurrencyRatesToAppDatabase(_rates.value!!) // Update database if API call is successful
            }
            checkFirstOpenStateAndUpdateUI() // Update UI if API call is successful

        } finally {
            _isUpdating.value = false
        }
    }

    fun refreshCurrencyRates() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchNewCurrencyRatesFromAPI()
        }
    }


    private suspend fun checkFirstOpenStateAndUpdateUI() {
       var isFirstOpen by Delegates.notNull<Boolean>()
        withContext(Dispatchers.IO) {
            isFirstOpen = repository.retrieveFirstOpenState()
        }

        if (_rates.value != null) {
            if (isFirstOpen) {
                // Handle first open logic
                var baseCurrencyKey = ""
                var baseCurrencyValue = "0"
                _rates.value?.rates?.keys?.forEach {
                    if (it == USD) {
                        baseCurrencyKey = it
                        baseCurrencyValue = _rates.value?.rates!![it]!!.toString()
                    }
                }
                var targetCurrencyKey = ""
                var targetCurrencyValue = "0"
                _rates.value?.rates?.keys?.forEach {
                    if (it == NGN) {
                        targetCurrencyKey = it
                        targetCurrencyValue = _rates.value?.rates!![it]!!.toString()
                    }
                }
                _baseCurrency.value = mapOf(baseCurrencyKey to baseCurrencyValue)
                _targetCurrency.value = mapOf(targetCurrencyKey to targetCurrencyValue)
                _currencyRatesLastUpdated.value = _rates.value?.time_last_update_utc ?: REFRESH
                Log.d("MY LOG", "value of _rates is ${_rates.value}")

                withContext(Dispatchers.IO) {
                    repository.saveFirstOpenState()
                    saveCurrencyPairsToAppDataStore()
                }
            } else {
                // Handle non-first open logic
                withContext(Dispatchers.IO) {
                    getCurrencyPairsFromAppDataStore()
                }
            }
        } else {
            // Handle failure to load any data
            _currencyRatesLastUpdated.value = REFRESH // update the ui with the refresh option
        }
    }

    private suspend fun getCurrencyPairsFromAppDataStore() {
       val currencyPairs = repository.retrieveCurrencyPairsFromAppDataStore().first()
       val retrievedBaseCurrencyFromDataStore = currencyPairs.first()
       val retrievedTargetCurrencyFromDataStore = currencyPairs.last()
        // update ui with saved currency pairs
        instantiateBaseCurrency(
            name = retrievedBaseCurrencyFromDataStore.keys.first(),
            value = retrievedBaseCurrencyFromDataStore.values.first()
        )
        instantiateTargetCurrency(
            name = retrievedTargetCurrencyFromDataStore.keys.first(),
            value = retrievedTargetCurrencyFromDataStore.values.first()
        )
        _currencyRatesLastUpdated.value = _rates.value?.time_last_update_utc ?: REFRESH
    }

    fun handleNewTargetCurrencyName(newTargetCurrencyName: String) {
        if (newTargetCurrencyName.isNotEmpty()) {
            val currency = newTargetCurrencyName.split(" ").last()
            val previousTargetCurrencyValue = _targetCurrency.value?.values?.first()
            instantiateTargetCurrency(name = currency, value = previousTargetCurrencyValue ?: "0.0")
            viewModelScope.launch {
                if (targetCurrencySelected.value) {
                    // if target currency is selected, only it's name would change,
                    // the value remains the same.
                    // while we'll compute the new value of the base currency
                    performConversionForTargetCurrency()

                } else {
                    // if the base currency is selected,
                    // the target currency name and value would change
                    performConversionForBaseCurrency()
                }
            }
        }
    }


    fun handleNewBaseCurrencyName(newBaseCurrency: String) {
        if (newBaseCurrency.isNotEmpty()) {
            val currency = newBaseCurrency.split(" ").last()
            val previousBaseCurrencyValue = _baseCurrency.value?.values?.first()
            instantiateBaseCurrency(name = currency, value = previousBaseCurrencyValue ?: "0.0")
            viewModelScope.launch {
                if (baseCurrencySelected.value) {
                    // if the base currency is selected,
                    // only the base currency name would change
                    // while we'll compute the value of the target currency
                    performConversionForBaseCurrency()
                } else {
                    // if the target currency is selected,
                    // target currency value and name would be retained, new base currency name would change
                    // and it's value recomputed
                    performConversionForTargetCurrency()
                }
            }

        }
    }

    private fun instantiateBaseCurrency(name: String, value: String) {
        _baseCurrency.value = mapOf(name to value)
    }

    private fun instantiateTargetCurrency(name: String, value: String) {
        _targetCurrency.value = mapOf(name to value)
    }

    fun handleBaseCurrencySelected() {
        if (targetCurrencySelected.value) {
            _targetCurrencySelected.value = false
        }
        _baseCurrencySelected.value = true
    }

    fun handleTargetCurrencySelected() {
        if (baseCurrencySelected.value) {
            _baseCurrencySelected.value = false
        }
        _targetCurrencySelected.value = true
    }

    private suspend fun saveCurrencyPairsToAppDataStore() {
        if (_baseCurrency.value != null && _targetCurrency.value != null) {
            repository.saveCurrencyPairsToAppDataStore(
                listOf(
                    _baseCurrency.value!!,
                    _targetCurrency.value!!
                )
            )
            Log.d("MY LOG", "SAVED TO DATA STORE COMPLETE")
        }
    }
}

