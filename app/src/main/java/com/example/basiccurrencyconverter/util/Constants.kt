package com.example.basiccurrencyconverter.util

object Constants {
//    val allCurrencies = listOf(
//        "Afghanistan Afghani AFN",
//        "Albania Lek ALL",
//        "Algeria Algerian Dinar DZD",
//        "Andorra Euro EUR",
//        "Angola Kwanza AOA",
//        "Antigua and Barbuda East Caribbean Dollar XCD",
//        "Argentina Argentine Peso ARS",
//        "Armenia Armenian Dram AMD",
//        "Australia Australian Dollar AUD",
//        "Austria Euro EUR",
//        "Azerbaijan Azerbaijani Manat AZN",
//        "Bahamas Bahamian Dollar BSD",
//        "Bahrain Bahraini Dinar BHD",
//        "Bangladesh Taka BDT",
//        "Barbados Barbadian Dollar BBD",
//        "Belarus Belarusian Ruble BYN",
//        "Belgium Euro EUR",
//        "Belize Belize Dollar BZD",
//        "Benin West African CFA Franc XOF",
//        "Bhutan Ngultrum BTN",
//        "Bolivia Boliviano BOB",
//        "Bosnia and Herzegovina Convertible Mark BAM",
//        "Botswana Pula BWP",
//        "Brazil Brazilian Real BRL",
//        "Brunei Brunei Dollar BND",
//        "Bulgaria Bulgarian Lev BGN",
//        "Burkina Faso West African CFA Franc XOF",
//        "Burundi Burundian Franc BIF",
//        "Cabo Verde Cape Verdean Escudo CVE",
//        "Cambodia Cambodian Riel KHR",
//        "Cameroon Central African CFA Franc XAF",
//        "Canada Canadian Dollar CAD",
//        "Central African Republic Central African CFA Franc XAF",
//        "Chad Central African CFA Franc XAF",
//        "Chile Chilean Peso CLP",
//        "China Renminbi (Yuan) CNY",
//        "Colombia Colombian Peso COP",
//        "Comoros Comorian Franc KMF",
//        "Congo, Democratic Republic of the Congolese Franc CDF",
//        "Congo, Republic of the Central African CFA Franc XAF",
//        "Costa Rica Costa Rican Colón CRC",
//        "Croatia Euro EUR",
//        "Cuba Cuban Peso CUP",
//        "Cyprus Euro EUR",
//        "Czech Republic Czech Koruna CZK",
//        "Denmark Danish Krone DKK",
//        "Djibouti Djiboutian Franc DJF",
//        "Dominica East Caribbean Dollar XCD",
//        "Dominican Republic Dominican Peso DOP",
//        "Ecuador US Dollar USD",
//        "Egypt Egyptian Pound EGP",
//        "El Salvador US Dollar USD",
//        "Equatorial Guinea Central African CFA Franc XAF",
//        "Eritrea Nakfa ERN",
//        "Estonia Euro EUR",
//        "Eswatini Lilangeni SZL",
//        "Ethiopia Ethiopian Birr ETB",
//        "Fiji Fijian Dollar FJD",
//        "Finland Euro EUR",
//        "France Euro EUR",
//        "Gabon Central African CFA Franc XAF",
//        "Gambia Gambian Dalasi GMD",
//        "Georgia Georgian Lari GEL",
//        "Germany Euro EUR",
//        "Ghana Ghanaian Cedi GHS",
//        "Greece Euro EUR",
//        "Grenada East Caribbean Dollar XCD",
//        "Guatemala Guatemalan Quetzal GTQ",
//        "Guinea Guinean Franc GNF",
//        "Guinea-Bissau West African CFA Franc XOF",
//        "Guyana Guyanese Dollar GYD",
//        "Haiti Haitian Gourde HTG",
//        "Honduras Honduran Lempira HNL",
//        "Hungary Hungarian Forint HUF",
//        "Iceland Icelandic Króna ISK",
//        "India Indian Rupee INR",
//        "Indonesia Indonesian Rupiah IDR",
//        "Iran Iranian Rial IRR",
//        "Iraq Iraqi Dinar IQD",
//        "Ireland Euro EUR",
//        "Israel Israeli New Shekel ILS",
//        "Italy Euro EUR",
//        "Jamaica Jamaican Dollar JMD",
//        "Japan Japanese Yen JPY",
//        "Jordan Jordanian Dinar JOD",
//        "Kazakhstan Kazakhstani Tenge KZT",
//        "Kenya Kenyan Shilling KES",
//        "Kiribati Australian Dollar AUD",
//        "Korea, North North Korean Won KPW",
//        "Korea, South South Korean Won KRW",
//        "Kosovo Euro EUR",
//        "Kuwait Kuwaiti Dinar KWD",
//        "Kyrgyzstan Kyrgyzstani Som KGS",
//        "Laos Lao Kip LAK",
//        "Latvia Euro EUR",
//        "Lebanon Lebanese Pound LBP",
//        "Lesotho Lesotho Loti LSL",
//        "Liberia Liberian Dollar LRD",
//        "Libya Libyan Dinar LYD",
//        "Liechtenstein Swiss Franc CHF",
//        "Lithuania Euro EUR",
//        "Luxembourg Euro EUR",
//        "Madagascar Malagasy Ariary MGA",
//        "Malawi Malawian Kwacha MWK",
//        "Malaysia Malaysian Ringgit MYR",
//        "Maldives Maldivian Rufiyaa MVR",
//        "Mali West African CFA Franc XOF",
//        "Malta Euro EUR",
//        "Marshall Islands US Dollar USD",
//        "Mauritania Mauritanian Ouguiya MRU",
//        "Mauritius Mauritian Rupee MUR",
//        "Mexico Mexican Peso MXN",
//        "Micronesia US Dollar USD",
//        "Moldova Moldovan Leu MDL",
//        "Monaco Euro EUR",
//        "Mongolia Mongolian Tögrög MNT",
//        "Montenegro Euro EUR",
//        "Morocco Moroccan Dirham MAD",
//        "Mozambique Mozambican Metical MZN",
//        "Myanmar Burmese Kyat MMK",
//        "Namibia Namibian Dollar NAD",
//        "Nauru Australian Dollar AUD",
//        "Nepal Nepalese Rupee NPR",
//        "Netherlands Euro EUR",
//        "New Zealand New Zealand Dollar NZD",
//        "Nicaragua Nicaraguan Córdoba NIO",
//        "Niger West African CFA Franc XOF",
//        "Nigeria Nigerian Naira NGN",
//        "North Macedonia Macedonian Denar MKD",
//        "Norway Norwegian Krone NOK",
//        "Oman Omani Rial OMR",
//        "Pakistan Pakistani Rupee PKR",
//        "Palau US Dollar USD",
//        "Panama Panamanian Balboa PAB",
//        "Papua New Guinea Papua New Guinean Kina PGK",
//        "Paraguay Paraguayan Guaraní PYG",
//        "Peru Peruvian Sol PEN",
//        "Philippines Philippine Peso PHP",
//        "Poland Polish Złoty PLN",
//        "Portugal Euro EUR",
//        "Qatar Qatari Riyal QAR",
//        "Romania Romanian Leu RON",
//        "Russia Russian Ruble RUB",
//        "Rwanda Rwandan Franc RWF",
//        "Saint Kitts and Nevis East Caribbean Dollar XCD",
//        "Saint Lucia East Caribbean Dollar XCD",
//        "Saint Vincent and the Grenadines East Caribbean Dollar XCD",
//        "Samoa Samoan Tala WST",
//        "San Marino Euro EUR",
//        "São Tomé and Príncipe São Tomé and Príncipe Dobra STN",
//        "Saudi Arabia Saudi Riyal SAR",
//        "Senegal West African CFA Franc XOF",
//        "Serbia Serbian Dinar RSD",
//        "Seychelles Seychellois Rupee SCR",
//        "Sierra Leone Sierra Leonean Leone SLE",
//        "Singapore Singapore Dollar SGD",
//        "Slovakia Euro EUR",
//        "Slovenia Euro EUR",
//        "Solomon Islands Solomon Islands Dollar SBD",
//        "Somalia Somali Shilling SOS",
//        "South Africa South African Rand ZAR",
//        "South Sudan South Sudanese Pound SSP",
//        "Spain Euro EUR",
//        "Sri Lanka Sri Lankan Rupee LKR",
//        "Sudan Sudanese Pound SDG",
//        "Suriname Surinamese Dollar SRD",
//        "Sweden Swedish Krona SEK",
//        "Switzerland Swiss Franc CHF",
//        "Syria Syrian Pound SYP",
//        "Taiwan New Taiwan Dollar TWD",
//        "Tajikistan Tajikistani Somoni TJS",
//        "Tanzania Tanzanian Shilling TZS",
//        "Thailand Thai Baht THB",
//        "Timor-Leste US Dollar USD",
//        "Togo West African CFA Franc XOF",
//        "Tonga Tongan Paʻanga TOP",
//        "Trinidad and Tobago Trinidad and Tobago Dollar TTD",
//        "Tunisia Tunisian Dinar TND",
//        "Turkey Turkish Lira TRY",
//        "Turkmenistan Turkmenistani Manat TMT",
//        "Tuvalu Australian Dollar AUD",
//        "Uganda Ugandan Shilling UGX",
//        "Ukraine Ukrainian Hryvnia UAH",
//        "United Arab Emirates UAE Dirham AED",
//        "United Kingdom Pound Sterling GBP",
//        "United States US Dollar USD",
//        "Uruguay Uruguayan Peso UYU",
//        "Uzbekistan Uzbekistani Som UZS",
//        "Vanuatu Vanuatu Vatu VUV",
//        "Vatican City Euro EUR",
//        "Venezuela Venezuelan Bolívar VES",
//        "Vietnam Vietnamese Đồng VND",
//        "Yemen Yemeni Rial YER",
//        "Zambia Zambian Kwacha ZMW",
//        "Zimbabwe Zimbabwean Dollar ZWL"
//
//    )
    val allCurrencies = listOf(
        "Afghanistan Afghani AFN",
        "Albania Lek ALL",
        "Algeria Algerian Dinar DZD",
        "Andorra Euro EUR",
        "Angola Kwanza AOA",
        "Antigua and Barbuda East Caribbean Dollar XCD",
        "Argentina Argentine Peso ARS",
        "Armenia Armenian Dram AMD",
        "Australia Australian Dollar AUD",
        "Austria Euro EUR",
        "Azerbaijan Azerbaijani Manat AZN",
        "Bahamas Bahamian Dollar BSD",
        "Bahrain Bahraini Dinar BHD",
        "Bangladesh Taka BDT",
        "Barbados Barbadian Dollar BBD",
        "Belarus Belarusian Ruble BYN",
        "Belgium Euro EUR",
        "Belize Belize Dollar BZD",
        "Benin West African CFA Franc XOF",
        "Bhutan Ngultrum BTN",
        "Bolivia Boliviano BOB",
        //        "Bosnia and Herzegovina Convertible Mark BAM",
        "Botswana Pula BWP",
        "Brazil Brazilian Real BRL",
        "Brunei Brunei Dollar BND",
        "Bulgaria Bulgarian Lev BGN",
        "Burkina Faso West African CFA Franc XOF",
        "Burundi Burundian Franc BIF",
        "Cabo Verde Cape Verdean Escudo CVE",
        "Cambodia Cambodian Riel KHR",
        "Cameroon Central African CFA Franc XAF",
        "Canada Canadian Dollar CAD",
        "Central African Republic Central African CFA Franc XAF",
        "Chad Central African CFA Franc XAF",
        "Chile Chilean Peso CLP",
        "China Renminbi (Yuan) CNY",
        "Colombia Colombian Peso COP",
        "Comoros Comorian Franc KMF",
        //        "Congo, Democratic Republic of the Congolese Franc CDF",
        "Congo, Republic of the Central African CFA Franc XAF",
        "Costa Rica Costa Rican Colón CRC",
        "Croatia Euro EUR",
        "Cuba Cuban Peso CUP",
        "Cyprus Euro EUR",
        "Czech Republic Czech Koruna CZK",
        "Denmark Danish Krone DKK",
        "Djibouti Djiboutian Franc DJF",
        "Dominica East Caribbean Dollar XCD",
        "Dominican Republic Dominican Peso DOP",
        "Ecuador US Dollar USD",
        "Egypt Egyptian Pound EGP",
        "El Salvador US Dollar USD",
        "Equatorial Guinea Central African CFA Franc XAF",
        "Eritrea Nakfa ERN",
        "Estonia Euro EUR",
        //        "Eswatini Lilangeni SZL",
        "Ethiopia Ethiopian Birr ETB",
        "Fiji Fijian Dollar FJD",
        "Finland Euro EUR",
        "France Euro EUR",
        "Gabon Central African CFA Franc XAF",
        //        "Gambia Gambian Dalasi GMD",
        "Georgia Georgian Lari GEL",
        "Germany Euro EUR",
        "Ghana Ghanaian Cedi GHS",
        "Greece Euro EUR",
        "Grenada East Caribbean Dollar XCD",
        "Guatemala Guatemalan Quetzal GTQ",
        //        "Guinea Guinean Franc GNF",
        "Guinea-Bissau West African CFA Franc XOF",
        "Guyana Guyanese Dollar GYD",
        //        "Haiti Haitian Gourde HTG",
        "Honduras Honduran Lempira HNL",
        "Hungary Hungarian Forint HUF",
        "Iceland Icelandic Króna ISK",
        "India Indian Rupee INR",
        "Indonesia Indonesian Rupiah IDR",
        "Iran Iranian Rial IRR",
        "Iraq Iraqi Dinar IQD",
        "Ireland Euro EUR",
        "Israel Israeli New Shekel ILS",
        "Italy Euro EUR",
        "Jamaica Jamaican Dollar JMD",
        "Japan Japanese Yen JPY",
        "Jordan Jordanian Dinar JOD",
        "Kazakhstan Kazakhstani Tenge KZT",
        "Kenya Kenyan Shilling KES",
        "Kiribati Australian Dollar AUD",
        //        "Korea, North North Korean Won KPW",
        "Korea, South South Korean Won KRW",
        "Kosovo Euro EUR",
        "Kuwait Kuwaiti Dinar KWD",
        "Kyrgyzstan Kyrgyzstani Som KGS",
        "Laos Lao Kip LAK",
        "Latvia Euro EUR",
        "Lebanon Lebanese Pound LBP",
        //        "Lesotho Lesotho Loti LSL",
        "Liberia Liberian Dollar LRD",
        "Libya Libyan Dinar LYD",
        "Liechtenstein Swiss Franc CHF",
        "Lithuania Euro EUR",
        "Luxembourg Euro EUR",
        "Madagascar Malagasy Ariary MGA",
        //        "Malawi Malawian Kwacha MWK",
        "Malaysia Malaysian Ringgit MYR",
        //        "Maldives Maldivian Rufiyaa MVR",
        "Mali West African CFA Franc XOF",
        "Malta Euro EUR",
        "Marshall Islands US Dollar USD",
        "Mauritania Mauritanian Ouguiya MRU",
        "Mauritius Mauritian Rupee MUR",
        "Mexico Mexican Peso MXN",
        "Micronesia US Dollar USD",
        "Moldova Moldovan Leu MDL",
        "Monaco Euro EUR",
        "Mongolia Mongolian Tögrög MNT",
        "Montenegro Euro EUR",
        "Morocco Moroccan Dirham MAD",
        "Mozambique Mozambican Metical MZN",
        "Myanmar Burmese Kyat MMK",
        "Namibia Namibian Dollar NAD",
        "Nauru Australian Dollar AUD",
        "Nepal Nepalese Rupee NPR",
        "Netherlands Euro EUR",
        "New Zealand New Zealand Dollar NZD",
        "Nicaragua Nicaraguan Córdoba NIO",
        "Niger West African CFA Franc XOF",
        "Nigeria Nigerian Naira NGN",
        "North Macedonia Macedonian Denar MKD",
        "Norway Norwegian Krone NOK",
        "Oman Omani Rial OMR",
        "Pakistan Pakistani Rupee PKR",
        "Palau US Dollar USD",
        "Panama Panamanian Balboa PAB",
        "Papua New Guinea Papua New Guinean Kina PGK",
        "Paraguay Paraguayan Guaraní PYG",
        "Peru Peruvian Sol PEN",
        "Philippines Philippine Peso PHP",
        "Poland Polish Złoty PLN",
        "Portugal Euro EUR",
        "Qatar Qatari Riyal QAR",
        "Romania Romanian Leu RON",
        "Russia Russian Ruble RUB",
        "Rwanda Rwandan Franc RWF",
        "Saint Kitts and Nevis East Caribbean Dollar XCD",
        "Saint Lucia East Caribbean Dollar XCD",
        "Saint Vincent and the Grenadines East Caribbean Dollar XCD",
        "Samoa Samoan Tala WST",
        "San Marino Euro EUR",
        "São Tomé and Príncipe São Tomé and Príncipe Dobra STN",
        "Saudi Arabia Saudi Riyal SAR",
        "Senegal West African CFA Franc XOF",
        "Serbia Serbian Dinar RSD",
        "Seychelles Seychellois Rupee SCR",
        //        "Sierra Leone Sierra Leonean Leone SLE",
        "Singapore Singapore Dollar SGD",
        "Slovakia Euro EUR",
        "Slovenia Euro EUR",
        "Solomon Islands Solomon Islands Dollar SBD",
        "Somalia Somali Shilling SOS",
        "South Africa South African Rand ZAR",
        "South Sudan South Sudanese Pound SSP",
        "Spain Euro EUR",
        //        "Sri Lanka Sri Lankan Rupee LKR",
        "Sudan Sudanese Pound SDG",
        "Suriname Surinamese Dollar SRD",
        "Sweden Swedish Krona SEK",
        "Switzerland Swiss Franc CHF",
        "Syria Syrian Pound SYP",
        "Taiwan New Taiwan Dollar TWD",
        "Tajikistan Tajikistani Somoni TJS",
        "Tanzania Tanzanian Shilling TZS",
        "Thailand Thai Baht THB",
        "Timor-Leste US Dollar USD",
        "Togo West African CFA Franc XOF",
        "Tonga Tongan Paʻanga TOP",
        //        "Trinidad and Tobago Trinidad and Tobago Dollar TTD",
        "Tunisia Tunisian Dinar TND",
        "Turkey Turkish Lira TRY",
        "Turkmenistan Turkmenistani Manat TMT",
        "Tuvalu Australian Dollar AUD",
        "Uganda Ugandan Shilling UGX",
        "Ukraine Ukrainian Hryvnia UAH",
        "United Arab Emirates UAE Dirham AED",
        "United Kingdom Pound Sterling GBP",
        "United States US Dollar USD",
        "Uruguay Uruguayan Peso UYU",
        "Uzbekistan Uzbekistani Som UZS",
        "Vanuatu Vanuatu Vatu VUV",
        "Vatican City Euro EUR",
        "Venezuela Venezuelan Bolívar VES",
        "Vietnam Vietnamese Đồng VND",
        "Yemen Yemeni Rial YER",
        "Zambia Zambian Kwacha ZMW",
        "Zimbabwe Zimbabwean Dollar ZWL"
    )
    const val USD = "USD"
    const val NGN = "NGN"
    const val CURRENCY_RATES = "currency_rates"
    const val NO_INTERNET = "No Internet"
    const val PREFERENCES_NAME = "currency_converter_datastore"
}