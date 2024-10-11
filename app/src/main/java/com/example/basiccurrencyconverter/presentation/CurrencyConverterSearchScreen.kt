package com.example.basiccurrencyconverter.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.basiccurrencyconverter.navigation.Screen
import com.example.basiccurrencyconverter.ui.theme.MEDIUM_PADDING
import com.example.basiccurrencyconverter.ui.theme.SMALL_PADDING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterSearchScreen(
    navController: NavHostController,
    viewModel: CurrencyConverterViewModel,
    coroutineScope: CoroutineScope
) {
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

    val searchInputText = viewModel.searchInputText.collectAsState().value
    val wasBaseCurrencyClicked by viewModel.wasBaseCurrencyExpandedToChangeItsName.collectAsState()
    val listBuilder = viewModel.listBuilder.collectAsState().value

    Column(
        modifier = Modifier
            .padding(SMALL_PADDING)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = searchInputText,
            onValueChange = {
                viewModel.updateSearchInputText(it)
            },
            singleLine = true,
            label = { },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search Icon")
            },
            placeholder = { },
            modifier = Modifier
                .fillMaxWidth(.8f),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            shape = RoundedCornerShape(30.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedTextColor = Color.Black,
                containerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = if (!isSystemInDarkTheme()) Color.Black else Color.White,
                unfocusedIndicatorColor = if (!isSystemInDarkTheme()) Color.Black else Color.White,
            ),
            trailingIcon = {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        if (searchInputText.isBlank()) {
                            navController.popBackStack()
                        } else {
                            viewModel.updateSearchInputText("")
                        }
                    }
                )
            }

        )

        if (searchInputText.isNotBlank()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MEDIUM_PADDING),
            ) {

                items(listBuilder) { currency ->
                    Text(
                        text = currency,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                Log.d("THE LOG", "currency clicked was $currency")
                                coroutineScope.launch {
                                    if (!wasBaseCurrencyClicked) {
                                        viewModel.handleNewTargetCurrencyName(currency)
                                    } else {
                                        viewModel.handleNewBaseCurrencyName(currency)
                                    }
                                }
                                navController.navigate(Screen.CurrencyConverterScreen.route)
                            }
                    )
                    Spacer(modifier = Modifier.padding(bottom = SMALL_PADDING))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.padding(top = SMALL_PADDING))
                }
            }
        }
    }
}


//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth(.8f)
//                .height(EXTRA_LARGE_PADDING)
//                .padding(5.dp)
//                .fillMaxWidth(),
//            value = searchInputText,
//            onValueChange = { viewModel.updateSearchInputText(it) },
//            placeholder = {
//                Text(
//                    modifier = Modifier
//                        .alpha(ContentAlpha.medium),
//                    text = "SEARCH_HERE",
//                    color = Color.White // Change placeholder color to white for better contrast
//                )
//            },
//            textStyle = TextStyle(
//                color = Color.Black // Set text color to white
//            ),
//            singleLine = true,
//            leadingIcon = {
//                Icon(
//                    modifier = Modifier
//                        .padding(end = LARGE_PADDING)
//                        .alpha(ContentAlpha.medium),
//                    imageVector = Icons.Default.Search,
//                    contentDescription = "",
//                    tint = Color.Black // Change icon color to white
//                )
//            },
//            trailingIcon = {
//                IconButton(
//                    onClick = {
//                        if (searchInputText.isNotEmpty()) {
////                            onTextChange("")
//                        } else {
////                            onCloseClicked()
//                        }
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Close,
//                        contentDescription = "",
//                        tint = Color.Black // Change close icon color to white
//                    )
//                }
//            },
//            keyboardOptions = KeyboardOptions(
//                imeAction = ImeAction.Search
//            ),
//            keyboardActions = KeyboardActions(
//                onSearch = {
////                    onSearchClicked(text)
//                }
//            ),
//            shape = RoundedCornerShape(MEDIUM_PADDING),
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = Color.Black,
//                unfocusedBorderColor = Color.Black,
//                cursorColor = Color.Black
//            )
//        )

