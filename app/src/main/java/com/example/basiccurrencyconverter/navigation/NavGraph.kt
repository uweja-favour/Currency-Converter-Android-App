package com.example.basiccurrencyconverter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.basiccurrencyconverter.presentation.CurrencyConverterMainScreen
import com.example.basiccurrencyconverter.presentation.CurrencyConverterSearchScreen
import com.example.basiccurrencyconverter.presentation.CurrencyConverterViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    viewModel: CurrencyConverterViewModel,
    coroutineScope: CoroutineScope
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CurrencyConverterScreen.route
    ) {
        composable(
            route = Screen.CurrencyConverterScreen.route
        ) {
            CurrencyConverterMainScreen(
                navController = navController,
                viewModel = viewModel,
                coroutineScope = coroutineScope
            )
        }

        composable(
            route = Screen.CurrencyConverterSearchScreen.route,
//            arguments = listOf(
//                navArgument("search_item") {
//                    type = NavType.StringType
//                }
//            )
        ) {
//            backStackEntry ->

//            backStackEntry.arguments?.let {
//                CurrencyConverterSearchScreen(
//                    navController = navController,
//                    searchItem = it.getString("search_item")
//                )
//            }
            CurrencyConverterSearchScreen(
                navController = navController,
                viewModel = viewModel,
                coroutineScope = coroutineScope
            )
        }
    }
}