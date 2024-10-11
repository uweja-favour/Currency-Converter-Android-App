package com.example.basiccurrencyconverter

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.basiccurrencyconverter.navigation.SetUpNavGraph
import com.example.basiccurrencyconverter.presentation.CurrencyConverterViewModel
import com.example.basiccurrencyconverter.ui.theme.BasicCurrencyConverterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var viewModel: CurrencyConverterViewModel
    private lateinit var coroutineScope: CoroutineScope
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            BasicCurrencyConverterTheme {
                navController = rememberNavController()
                viewModel = hiltViewModel()
                coroutineScope = rememberCoroutineScope()
//                SetUpNavGraph()
                SetUpNavGraph(
                    navController = navController,
                    viewModel = viewModel,
                    coroutineScope = coroutineScope
                )
            }
        }
    }
}
