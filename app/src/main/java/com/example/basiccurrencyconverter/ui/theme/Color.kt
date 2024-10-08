package com.example.basiccurrencyconverter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val OrangeColor = Color(0xFFFFA500)

val VerticalSwapIconColor
    @Composable
    get() = if (!isSystemInDarkTheme()) Color.DarkGray
            else Color.White

val TextColor
    @Composable
    get() = if (!isSystemInDarkTheme()) Color.DarkGray
    else Color.White

