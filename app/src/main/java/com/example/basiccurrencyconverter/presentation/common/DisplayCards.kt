package com.example.basiccurrencyconverter.presentation.common

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basiccurrencyconverter.ui.theme.OrangeColor
import com.example.basiccurrencyconverter.ui.theme.VerticalSwapIconColor
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun HorizontalDisplayCard(
    modifier: Modifier = Modifier,
    isSelected: MutableStateFlow<Boolean>,
    currencyInValue: Number,
    currencyType: String,
    locationIcon: Boolean = false,
    handleClick: () -> Unit
) {

    val hasBeenSelected = isSelected.collectAsState()
    LaunchedEffect(key1 = hasBeenSelected) {
        Log.d("MY LOG", "isSelected changed")
    }

    Surface(
        modifier = modifier
            .height(90.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, top = 15.dp, bottom = 15.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        handleClick()
                    }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currencyType,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis
                )

                if (locationIcon) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "",
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    Icons.Default.ArrowForwardIos,
                    contentDescription = "",
                    modifier = Modifier
                        .size(15.dp)
                )
            }
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom
            ) {
                item {
                    Text(
                        text = currencyInValue.toString(),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                        fontSize = calculateFontSize(currencyInValue.toString()), // Dynamic font size
                        overflow = TextOverflow.Ellipsis,
                        color = if (hasBeenSelected.value) OrangeColor else Color.Black,
                        modifier = Modifier.weight(1f) // Allow this text to take available space
                    )
                }
            }
        }
    }
}

// Function to calculate font size based on the length of the string
fun calculateFontSize(value: String): TextUnit {
    val baseSize = 30.sp // Base font size
    val maxLength = 10 // Maximum length for full size
    val sizeFactor = 5 // Size decrease factor per character


    return if (value.length > maxLength) {
//        baseSize - (value.length - maxLength).sp * sizeFactor
        val textUnit = baseSize.value - (value.length - maxLength) * sizeFactor
        if (textUnit.sp < 25.sp) {
            25.sp
        } else {
            textUnit.sp
        }
    } else {
        baseSize
    }
}

@Composable
fun VerticalDisplayCard(
    modifier: Modifier,
    height: Dp
) {
    Surface(
        modifier = modifier
            .height(height)
            .width(90.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Icon(
                Icons.Default.SwapVert,
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(30.dp),
                tint = VerticalSwapIconColor
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewFunction() {
//    VerticalDisplayCard()
}