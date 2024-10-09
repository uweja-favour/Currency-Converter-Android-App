package com.example.basiccurrencyconverter.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basiccurrencyconverter.presentation.common.HorizontalDisplayCard
import com.example.basiccurrencyconverter.presentation.common.VerticalDisplayCard
import com.example.basiccurrencyconverter.ui.theme.TextColor
import com.example.basiccurrencyconverter.util.Constants.allCurrencies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
//    val showBottomSheet by viewModel.showBottomSheet.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberBottomSheetScaffoldState()
    val lastUpdated by viewModel.lastUpdated.collectAsState()
    var wasBaseCurrencyClicked by rememberSaveable {
        mutableStateOf(false)
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetContent = {
            CurrencyListContent(
                onClick = { theCurrencyClicked ->
                    if (!wasBaseCurrencyClicked) {
                        coroutineScope.launch {
                            bottomSheetState.bottomSheetState.partialExpand() // drop bottom sheet
                        }
                        viewModel.handleNewTargetCurrencyName(theCurrencyClicked)
                    } else {
                        coroutineScope.launch {
                            bottomSheetState.bottomSheetState.partialExpand()
                        }
                        viewModel.handleNewBaseCurrencyName(theCurrencyClicked)
                    }
                }
            )
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
                onClick = { baseCurrencyClicked ->
                   wasBaseCurrencyClicked = baseCurrencyClicked
                },
                bottomSheetState = bottomSheetState,
                coroutineScope = coroutineScope
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))

            if (lastUpdated.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Last updated: $lastUpdated",
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            viewModel.fetchNewCurrencyRatesFromInternet()
                        }
                    )
                }
            }
            AnimatedVisibility(
                visible = viewModel.baseCurrencySelected.collectAsState().value || viewModel.targetCurrencySelected.collectAsState().value
            ) {
//
//                NumericKeyboard(
//                    viewModel = viewModel
//                )
                val stringBuilder = StringBuilder("")
                SecondNumericKeyboard {
                    when(it) {
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
                            if (stringBuilder[stringBuilder.length - 1] != '.') {
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

fun updateCurrencyValue(string: String, viewModel: HomeViewModel) {
    if (viewModel.baseCurrencySelected.value) {
        viewModel.updateBaseCurrencyInput(string)
    } else {
        viewModel.updateTargetCurrencyInput(string)
    }
}

@Composable
fun CurrencyListContent(
    onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        item {
            Column(modifier = Modifier.fillParentMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Select currency",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.padding(bottom = 10.dp))
        }

        items(allCurrencies) { currency ->
            Text(
                modifier = Modifier.clickable {
                    onClick(currency)
                },
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

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDisplaySection(
    viewModel: HomeViewModel,
    onClick: (Boolean) -> Unit,
    bottomSheetState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope

) {
    val baseCurrencyDisplayName = viewModel.baseCurrency.collectAsState().value?.keys?.first()
    val targetCurrencyDisplayName = viewModel.targetCurrency.collectAsState().value?.keys?.first()

    val baseCurrencyDisplayValue = viewModel.baseCurrency.collectAsState().value?.values?.first()
    val targetCurrencyDisplayValue = viewModel.targetCurrency.collectAsState().value?.values?.first()

    val baseCurrencySelected = viewModel.baseCurrencySelected
    val targetCurrencySelected = viewModel.targetCurrencySelected


    Row(
        modifier = Modifier
            .fillMaxHeight(0.32f)
            .padding(15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HorizontalDisplayCard(
                modifier = Modifier.clickable {
                    if(targetCurrencySelected.value) {
                        viewModel.setTargetCurrencySelectedToFalse()
                        viewModel.setBaseCurrencySelectedToTrue()
                    } else {
                        viewModel.setBaseCurrencySelectedToTrue()
                    }
                },
                isSelected = baseCurrencySelected,
                currencyName = baseCurrencyDisplayName ?: "issue is here",
                currencyInValue = baseCurrencyDisplayValue ?: 0.100,
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
                    if(viewModel.baseCurrencySelected.value) {
                        viewModel.setBaseCurrencySelectedToFalse()
                        viewModel.setTargetCurrencySelectedToTrue()
                    } else {
                        viewModel.setTargetCurrencySelectedToTrue()
                    }
                },
                isSelected = targetCurrencySelected,
                currencyName = targetCurrencyDisplayName ?: "issue is here",
                currencyInValue =  targetCurrencyDisplayValue ?: 0.100,
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
                viewModel.fetchNewCurrencyRatesFromInternet()
            },
            height = 191.dp
        )
    }
}
//
//
//@SuppressLint("StateFlowValueCalledInComposition")
//@Composable
//private fun NumericKeyboard(
//    viewModel: HomeViewModel
//) {
//    val focusRequester = remember { FocusRequester() }
//    val focusManager = LocalFocusManager.current
//
//    OutlinedTextField(
//        value = if (viewModel.baseCurrencySelected.value) viewModel.baseCurrencyInput else viewModel.targetCurrencyInput,
//        onValueChange = {
//            if (viewModel.baseCurrencySelected.value) {
//                viewModel.updateBaseCurrencyInput(it)
//            } else {
//                viewModel.updateTargetCurrencyInput(it)
//            }
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .focusRequester(focusRequester)
//            .clickable {
//                focusRequester.requestFocus()
//            },
//        keyboardOptions = KeyboardOptions.Default.copy(
//            keyboardType = KeyboardType.Number
//        ),
//        label = { Text("Enter Number") },
//        // This button will clear focus when clicked, hiding the keyboard
//        trailingIcon = {
//            Icon(
//                imageVector = Icons.Default.Clear,
//                contentDescription = "Clear",
//                modifier = Modifier.clickable {
//                    focusManager.clearFocus()
//                }
//            )
//        }
//    )
//
//    LaunchedEffect(Unit) {
//        focusRequester.requestFocus()
//    }
//}





@Composable
fun SecondNumericKeyboard(
    onKeyPress: (String) -> Unit,
) {
    val keys = listOf(
        listOf("1", "2", "3", "AC"),
        listOf("4", "5", "6", "⌫"),
        listOf("7", "8", "9"),
        listOf("00", "0", ".")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 5.dp)
            ) {
                row.forEach { key ->
                    Button(
                        onClick = { onKeyPress(key) },
                        modifier = Modifier.size(90.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimary),
                        elevation = ButtonDefaults.buttonElevation(1.5.dp)
                    ) {
                        Text(
                            text = key,
                            color = TextColor,
                            textAlign = TextAlign.Center,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    }
                }
            }
        }
    }
}