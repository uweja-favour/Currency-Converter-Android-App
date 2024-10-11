package com.example.basiccurrencyconverter.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.basiccurrencyconverter.navigation.Screen
import com.example.basiccurrencyconverter.presentation.common.HorizontalDisplayCard
import com.example.basiccurrencyconverter.presentation.common.VerticalDisplayCard
import com.example.basiccurrencyconverter.ui.theme.EXTRA_SMALL_PADDING
import com.example.basiccurrencyconverter.ui.theme.MEDIUM_PADDING
import com.example.basiccurrencyconverter.ui.theme.TextColor
import com.example.basiccurrencyconverter.util.Constants.allCurrencies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun LoadingScreen() {
    // Simple placeholder UI
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}



@SuppressLint("CoroutineCreationDuringComposition", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterMainScreen(
    navController: NavHostController,
    viewModel: CurrencyConverterViewModel,
    coroutineScope: CoroutineScope
) {

    LaunchedEffect(Unit) {
        viewModel.initializeDataIfNeeded()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(text = "Currency Converter")
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            ScreenContents(
                navController = navController,
                viewModel = viewModel,
                coroutineScope = coroutineScope
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenContents(
    navController: NavHostController,
    viewModel: CurrencyConverterViewModel,
    coroutineScope: CoroutineScope
) {


    val bottomSheetState = rememberBottomSheetScaffoldState()
    val currencyRatesLastUpdated by viewModel.currencyRatesLastUpdated.collectAsState()
    val wasBaseCurrencyExpandedToChangeItsName by viewModel.wasBaseCurrencyExpandedToChangeItsName.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()


    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetContent = {
            if (bottomSheetState.bottomSheetState.hasExpandedState) {
                CurrencyListContent(
                    navController = navController,
                    onClick = { theCurrencyClicked ->
                        coroutineScope.launch {
                            bottomSheetState.bottomSheetState.partialExpand()
                        }
                        if (!wasBaseCurrencyExpandedToChangeItsName) {
                            viewModel.handleNewTargetCurrencyName(theCurrencyClicked)
                        } else {
                            viewModel.handleNewBaseCurrencyName(theCurrencyClicked)
                        }
                    }
                )
            }
        },
        sheetPeekHeight = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            CurrencyDisplaySection(
                viewModel = viewModel,
                onClick = { baseCurrencyClicked -> viewModel.updateIfBaseCurrencyWasClicked(baseCurrencyClicked)  },
                bottomSheetState = bottomSheetState,
                coroutineScope = coroutineScope
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))

            if (currencyRatesLastUpdated.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isUpdating) "Updating..." else "Last updated: $currencyRatesLastUpdated"
                    )
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.clickable {
                            viewModel.refreshCurrencyRates()
                        }
                    )
                }
            }
            var includedDecimal by rememberSaveable {
                mutableStateOf(false)
            }
            LaunchedEffect(
                key1 = viewModel.baseCurrencySelected.collectAsState().value,
                key2 = viewModel.targetCurrencySelected.collectAsState().value
            ) {
                includedDecimal = false
            }

            AnimatedVisibility(
                visible = viewModel.baseCurrencySelected.collectAsState().value || viewModel.targetCurrencySelected.collectAsState().value
            ) {
                val stringBuilder = StringBuilder("")

                SecondNumericKeyboard {
                    when (it) {
                        "AC" -> {
                            stringBuilder.clear()
                            Log.d("MY NUMBERS", "string builder is ${stringBuilder.toString()} and $stringBuilder")
                        }
                        "⌫" -> {
                            if (stringBuilder.isNotBlank()) {
                                stringBuilder.deleteCharAt(stringBuilder.length - 1)
                            }
                        }
                        "." -> {
                            if (stringBuilder.isNotBlank() && !includedDecimal) {
                                stringBuilder.append(it)
                                includedDecimal = true
                            }
                        }
                        "0" -> {
                            if (stringBuilder.toString() != "0" && stringBuilder.isNotBlank()) {
                                stringBuilder.append(it)
                            }
                        }
                        "00" -> {
                            if (stringBuilder.toString() != "0" && stringBuilder.isNotBlank()) {
                                stringBuilder.append(it)
                            }
                        }
                        else -> {
                            stringBuilder.append(it)
                        }
                    }
                    updateCurrencyValue(
                        string = stringBuilder.toString(),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}


private fun updateCurrencyValue(
    string: String,
    viewModel: CurrencyConverterViewModel
) {
    if (viewModel.baseCurrencySelected.value) {
        viewModel.updateBaseCurrencyInput(string)
    } else {
        viewModel.updateTargetCurrencyInput(string)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyListContent(
    navController: NavHostController,
    onClick: (String) -> Unit
) {
//    var text by remember { mutableStateOf("") } // Start with an empty string

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select currency",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(bottom = 10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(.8f),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .clickable {
                        navController.navigate(Screen.CurrencyConverterSearchScreen.route)
                    }
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(MEDIUM_PADDING)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .padding(
                            horizontal = EXTRA_SMALL_PADDING,
                            vertical = EXTRA_SMALL_PADDING
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                    Text(
                        text = "Search",
                        fontWeight = FontWeight.Medium

                    )
                }

            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MEDIUM_PADDING),
        ) {
            items(allCurrencies) { currency ->
                Text(
                    modifier = Modifier.clickable { onClick(currency) },
                    text = currency,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(bottom = 10.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.padding(top = 10.dp))
            }
        }
    }


}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDisplaySection(
    viewModel: CurrencyConverterViewModel,
    onClick: (Boolean) -> Unit,
    bottomSheetState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope
) {
    val baseCurrencyDisplayName = viewModel.baseCurrency.collectAsState().value?.keys?.first() ?: "N/A"
    val targetCurrencyDisplayName = viewModel.targetCurrency.collectAsState().value?.keys?.first() ?: "N/A"

    val baseCurrencyDisplayValue = viewModel.baseCurrency.collectAsState().value?.values?.first() ?: "100.5"
    val targetCurrencyDisplayValue = viewModel.targetCurrency.collectAsState().value?.values?.first() ?: "100.5"

    val baseCurrencySelected = viewModel.baseCurrencySelected
    val targetCurrencySelected = viewModel.targetCurrencySelected

    Row(
        modifier = Modifier
            .fillMaxHeight(0.32f)
            .padding(15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.7f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HorizontalDisplayCard(
                modifier = Modifier.clickable {
                    coroutineScope.launch(Dispatchers.Main) {
                        viewModel.handleBaseCurrencySelected()
                    }
                },
                isSelected = baseCurrencySelected,
                currencyName = baseCurrencyDisplayName,
                currencyInValue = baseCurrencyDisplayValue,
                locationIcon = true,
                handleClick = {
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.expand()
                    }
                    onClick(true)
                }
            )

            HorizontalDisplayCard(
                modifier = Modifier.clickable {
                    coroutineScope.launch(Dispatchers.Main) {
                        viewModel.handleTargetCurrencySelected()
                    }
                },
                isSelected = targetCurrencySelected,
                currencyName = targetCurrencyDisplayName,
                currencyInValue = targetCurrencyDisplayValue,
                locationIcon = false,
                handleClick = {
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.expand()
                    }
                    onClick(false)
                }
            )
        }

        VerticalDisplayCard(
            modifier = Modifier.clickable {
                viewModel.refreshCurrencyRates()
            },
            height = 191.dp
        )
    }
}

@Composable
fun SecondNumericKeyboard(
    onKeyPress: (String) -> Unit,
) {
    val keyboardKeys = listOf(
        listOf("1", "2", "3", "AC"),
        listOf("4", "5", "6", "⌫"),
        listOf("7", "8", "9"),
        listOf("00", "0", ".")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        keyboardKeys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 20.dp)
            ) {
                row.forEach { key ->
                    Button(
                        onClick = { onKeyPress(key) },
                        modifier = Modifier.size(70.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimary),
                        elevation = ButtonDefaults.buttonElevation(1.5.dp)
                    ) {
                        Text(
                            text = key,
                            color = TextColor,
                            textAlign = TextAlign.Center,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    }
                }
            }
        }
    }
}
