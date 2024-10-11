package com.example.basiccurrencyconverter

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.basiccurrencyconverter.navigation.SetUpNavGraph
import com.example.basiccurrencyconverter.presentation.CurrencyConverterMainScreen
import com.example.basiccurrencyconverter.presentation.CurrencyConverterViewModel
import com.example.basiccurrencyconverter.ui.theme.BasicCurrencyConverterTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only enable StrictMode in debug builds
//        if (true) {
//            // Enable Thread Policy
//            StrictMode.setThreadPolicy(
//                StrictMode.ThreadPolicy.Builder()
//                    .detectAll()   // Detect all thread violations (network, disk read/write, etc.)
//                    .penaltyLog()  // Log the violations to logcat
//                    .penaltyDeath() // Crash the app for any detected violations (useful for testing)
//                    .build()
//            )
//
//            // Enable VM Policy
//            StrictMode.setVmPolicy(
//                StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()   // Detect unclosed SQLite database objects
//                    .detectLeakedClosableObjects()  // Detect unclosed I/O streams (like files)
//                    .penaltyLog()  // Log the violations to logcat
//                    .penaltyDeath() // Crash the app for any detected violations (useful for testing)
//                    .build()
//            )
//        }


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContent {
            BasicCurrencyConverterTheme {
//                viewModel.fetchNewCurrencyRatesFromInternet(this)
                val navController = rememberNavController()
                val viewModel: CurrencyConverterViewModel = hiltViewModel()
                val coroutineScope = rememberCoroutineScope()
                SetUpNavGraph(navController = navController, viewModel = viewModel, coroutineScope = coroutineScope)
            }
        }
    }
}
