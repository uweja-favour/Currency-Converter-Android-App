package com.example.basiccurrencyconverter.navigation

sealed class Screen(val route: String) {
    data object CurrencyConverterScreen : Screen("currency_converter_screen")
    data object CurrencyConverterSearchScreen : Screen("currency_converter_search_screen") {
//        fun passTheSearchItem(string: String): String {
//            return "currency_converter_search_screen/{search_item}"
//        }
    }
}
//} navController.navigate(Screen.Details.passHeroId(heroId = hero.id))