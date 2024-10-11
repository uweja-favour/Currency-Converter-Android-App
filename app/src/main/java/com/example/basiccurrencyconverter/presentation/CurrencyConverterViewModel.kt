package com.example.basiccurrencyconverter.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basiccurrencyconverter.data.repository.Repository
import com.example.basiccurrencyconverter.domain.model.CurrencyRates
import com.example.basiccurrencyconverter.util.Constants.NGN
import com.example.basiccurrencyconverter.util.Constants.USD
import com.example.basiccurrencyconverter.util.Constants.allCurrencies
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
    val rates = _rates

    // BottomSheet state
    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet

    // Currencies state
    private val _baseCurrency = MutableStateFlow<Map<String, Double>?>(null)
    val baseCurrency = _baseCurrency

    private val _targetCurrency = MutableStateFlow<Map<String, Double>?>(null)
    val targetCurrency = _targetCurrency

    private val _currencyRatesLastUpdated = MutableStateFlow("")
    val currencyRatesLastUpdated = _currencyRatesLastUpdated

    // Inputs and values
    var baseCurrencyInput = MutableStateFlow("0")
    var targetCurrencyInput = MutableStateFlow("0")

    // Track if currencies were selected
    private val _wasBaseCurrencyExpandedToChangeItsName = MutableStateFlow(false)
    val wasBaseCurrencyExpandedToChangeItsName = _wasBaseCurrencyExpandedToChangeItsName

    private val _baseCurrencySelected = MutableStateFlow(false)
    val baseCurrencySelected = _baseCurrencySelected

    private val _targetCurrencySelected = MutableStateFlow(false)
    val targetCurrencySelected = _targetCurrencySelected

    // Debounce job for search input
    private var searchJob: Job? = null
    var searchInputText = MutableStateFlow("")

    // Currency list
    private val _listBuilder = MutableStateFlow(emptyList<String>())
    val listBuilder = _listBuilder

    init {
        viewModelScope.launch { // runs on the 'Main' AKA ui thread
            initializeData()
            _baseCurrencySelected.value = true
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
        if (input.isNotBlank()) {
            viewModelScope.launch {
                // update base currency with new value
                _baseCurrency.value = mapOf(
                    (_baseCurrency.value?.keys?.first() ?: "LINE 121")
                    to
                    baseCurrencyInput.value.toDouble()
                )
                performConversionForBaseCurrency()
            }
        }
    }

    /**
     * Simplified function for updating target currency input and triggering conversion.
     * Avoided multiple state updates and redundant coroutines.
     */
    fun updateTargetCurrencyInput(input: String) {
        targetCurrencyInput.value = input.ifBlank { "0" }
        if (input.isNotBlank()) {
            viewModelScope.launch {
                // update target currency with new value
                _targetCurrency.value = mapOf(
                    (_targetCurrency.value?.keys?.first() ?: "LINE 134")
                    to
                    targetCurrencyInput.value.toDouble()
                )
                performConversionForTargetCurrency()
            }
        }
    }



    /**
     * Combined the logic for currency conversion into one function to avoid redundancy.
     */
    private suspend fun performConversionForBaseCurrency() {
        withContext(Dispatchers.Default) {
            val baseCurrency = _baseCurrency.value?.keys?.firstOrNull()
            val baseValue = _baseCurrency.value?.values?.first()

            if (baseCurrency == USD) {
                convertBaseToTarget(baseValue!!)
            } else {
                convertNonUSDToTarget(baseValue!!)
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

        // update _targetCurrency value
        _targetCurrency.value = mapOf((targetCurrencyName ?: "THE ERROR HERE!") to targetCurrencyInput.value.toDouble())
        if (_targetCurrency.value?.keys?.first() != "THE ERROR HERE!") {
            withContext(Dispatchers.IO) {
                saveCurrencyPairsToAppDataStore()
            }
        } else {
            Log.d("MY LOG", "There is an error on line 172")
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

    private suspend fun convertTargetToBase(targetValue: Double) {
        // Convert targetCurrency from USD to baseCurrency
        val baseCurrencyName = _baseCurrency.value?.keys?.first()
        val conversionRate = _rates.value?.rates?.get(baseCurrencyName)

        // Multiply targetValue (USD) by the conversion rate to get the base currency value
        val baseValue = conversionRate?.let { targetValue * it } ?: 0.0
        baseCurrencyInput.value = baseValue.toString()
        // update _baseCurrency value
        _baseCurrency.value = mapOf((baseCurrencyName ?: "THE ERROR HERE!") to baseCurrencyInput.value.toDouble())

        if (_baseCurrency.value?.keys?.first() != "THE ERROR HERE!") {
            withContext(Dispatchers.IO) {
                saveCurrencyPairsToAppDataStore()
            }
        } else {
            Log.d("MY LOG", "There is an error on line 220")
        }
    }


    private suspend fun convertNonUSDTargetToBase(targetValue: Double) {
        // Convert targetCurrency to USD first, before converting to baseCurrency
        val targetCurrencyName = _targetCurrency.value?.keys?.first()
        val conversionRate = _rates.value?.rates?.get(targetCurrencyName)

        val targetInUSD = conversionRate?.let { targetValue / it } ?: 0.0
        convertTargetToBase(targetInUSD)
    }


    /**
     * Initializes the ViewModel's data. Checks if the app is opened for the first time.
     */
    private suspend fun initializeData() {
        withContext(Dispatchers.IO) {
            val cachedRates = repository.getRatesFromAppDatabase()
            _rates.value = cachedRates

            if (_rates.value == null) {
                Log.d("MY LOG", "Fetching data from api, because database was empty...")
                fetchNewCurrencyRatesFromAPI()
            } else {
                // if database data is valid
                checkFirstOpenState()
            }
        }
    }

    /**
     * Fetches new currency rates from the internet and updates the local database.
     */
    suspend fun fetchNewCurrencyRatesFromAPI() {
        val newRates = repository.fetchRatesFromInternet(context)
        _rates.value = newRates

        if (_rates.value != null) {
            repository.saveCurrencyRatesToAppDatabase(_rates.value!!) // update Database if api call is successful
            checkFirstOpenState() // update ui if api call is successful
            Log.d("MY LOG", "saved rates to database!")
        }
    }


    private suspend fun checkFirstOpenState() {
       var isFirstOpen by Delegates.notNull<Boolean>()
        withContext(Dispatchers.IO) {
            isFirstOpen = repository.retrieveFirstOpenState()
        }

        if (isFirstOpen && _rates.value != null) {
        // Handle first open logic
            var baseCurrencyKey = ""
            var baseCurrencyValue = 0.0
            _rates.value?.rates?.keys?.forEach {
                if (it == USD) {
                    baseCurrencyKey = it
                    baseCurrencyValue = _rates.value?.rates!![it]!!
                }
            }
            var targetCurrencyKey = ""
            var targetCurrencyValue = 0.0
            _rates.value?.rates?.keys?.forEach {
                if (it == NGN) {
                    targetCurrencyKey = it
                    targetCurrencyValue = _rates.value?.rates!![it]!!
                }
            }
            _baseCurrency.value = mapOf(baseCurrencyKey to baseCurrencyValue)
            _targetCurrency.value = mapOf(targetCurrencyKey to targetCurrencyValue)
            _currencyRatesLastUpdated.value = _rates.value?.time_last_update_utc ?: ""
            Log.d("MY LOG", "value of _rates is ${_rates.value}")

            withContext(Dispatchers.IO) {
                repository.saveFirstOpenState()
                saveCurrencyPairsToAppDataStore()
            }
        } else {
            withContext(Dispatchers.IO) {
                getCurrencyPairsFromAppDataStore()
            }
        }
    }

    private suspend fun getCurrencyPairsFromAppDataStore() {
        if (_rates.value != null) {
           val currencyPairs = repository.retrieveCurrencyPairsFromAppDataStore().first()
           val retrievedBaseCurrencyFromDataStore = currencyPairs.first()
           val retrievedTargetCurrencyFromDataStore = currencyPairs.last()
            // update ui with saved currency pairs
            _baseCurrency.value =
                mapOf(retrievedBaseCurrencyFromDataStore.keys.first() to retrievedBaseCurrencyFromDataStore.values.first())
            _targetCurrency.value =
                mapOf(retrievedTargetCurrencyFromDataStore.keys.first() to retrievedTargetCurrencyFromDataStore.values.first())
            _currencyRatesLastUpdated.value = _rates.value?.time_last_update_utc!!
            Log.d("MY LOG", "YOUR VALUES ARE _baseCurrency: ${_baseCurrency.value} and _targetCurrency: ${_targetCurrency.value}")
            Log.d(
                "MY LOG",
                "updateCurrencyPairsFromDataStore function has successfully COMPLETED"
            )
        }
    }
//
//    private fun updateUI(newCurrencyRates: CurrencyRates) {
//        Log.d("NEW LOG", "value of firstOpenState is $firstOpenState")
//        viewModelScope.launch(Dispatchers.IO) {
//            _rates.value = newCurrencyRates
//            if (!firstOpenState!!) {
//                // handle app first open state
//                Log.d("NEW LOG", "value of firstOpenState is $firstOpenState")
//                var baseCurrencyKey = ""
//                _rates.value?.rates?.keys?.forEach {
//                    if (it == USD) {
//                        baseCurrencyKey = it
//                        baseCurrencyValue = _rates.value?.rates!![it]!!
//                    }
//                }
//                var targetCurrencyKey = ""
//                _rates.value?.rates?.keys?.forEach {
//                    if (it == NGN) {
//                        targetCurrencyKey = it
//                        targetCurrencyValue = _rates.value?.rates!![it]!!
//                    }
//                }
//                _baseCurrency.value = mapOf(baseCurrencyKey to baseCurrencyValue)
//                _targetCurrency.value = mapOf(targetCurrencyKey to targetCurrencyValue)
//                _currencyRatesLastUpdated.value = _rates.value?.time_last_update_utc!!
//                repository.saveFirstOpenState()
//                saveCurrencyPairsToAppDataStore()
//            } else {
//                // app has been open before so...
//                getCurrencyPairsFromAppDataStore()
//            }
//        }
//    }
//

    suspend fun handleNewTargetCurrencyName(newTargetCurrencyName: String) {
        if (newTargetCurrencyName.isNotEmpty()) {
            val currency = newTargetCurrencyName.split(" ").last()
            if (_targetCurrencySelected.value) {
                // if target currency is selected, only it's name would change,
                // the value remains the same.
                // while we'll compute the new value of the base currency
                val previousTargetCurrencyValue = _targetCurrency.value?.values?.first()
                _targetCurrency.value = mapOf(currency to (previousTargetCurrencyValue!!))
                performConversionForTargetCurrency()
            } else {
                // if the base currency is selected,
                // the target currency name and value would change
                val previousTargetCurrencyValue = _targetCurrency.value?.values?.first()
                _targetCurrency.value = mapOf(currency to (previousTargetCurrencyValue!!))
                performConversionForBaseCurrency()
            }
            Log.d("THE LOG", "In handleNewTargetCurrency, the function completed")
        }
    }


    suspend fun handleNewBaseCurrencyName(newBaseCurrency: String) {
        if (newBaseCurrency.isNotEmpty()) {
            val currency = newBaseCurrency.split(" ").last()
            if (_baseCurrencySelected.value) {
                val previousBaseCurrencyValue = _baseCurrency.value?.values?.first()
                _baseCurrency.value = mapOf(currency to (previousBaseCurrencyValue ?: 0.0))
                performConversionForBaseCurrency()
            } else {
                // if the _targetCurrency is selected
                // target currency value and name would be retained, new base currency name would change
                // and it's value recomputed
                val previousBaseCurrencyValue = _baseCurrency.value?.values?.first()
                _baseCurrency.value = mapOf(currency to (previousBaseCurrencyValue ?: 0.0))
                performConversionForTargetCurrency()
            }
            // re-create the base currency with it's new name first
            Log.d("THE LOG", "In handleNewBaseCurrency, the function completed")
        }
    }

    fun handleBaseCurrencySelected() {
        if (_targetCurrencySelected.value) {
            _targetCurrencySelected.value = false
        }
        _baseCurrencySelected.value = true
    }

    fun handleTargetCurrencySelected() {
        if (_baseCurrencySelected.value) {
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


//
//    private val _swapConversion = MutableStateFlow(false)
//    private val swapConversion = _swapConversion

//    private fun getRates(): Flow<CurrencyRates> {
//        return repository.getRates().also {
//            Log.d("MY LOG", "Rates fetched successfully: $it")
//        }
//
//    }
}

