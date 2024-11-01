package com.example.weatherapp.database

data class CountryResponse(
    val id: Int,
    val name: String,
    val iso3: String,
    val numeric_code: String,
    val iso2: String,
    val phonecode: String,
    val capital: String,
    val currency: String,
    val currency_name: String,
    val currency_symbol: String,
    val tld: String,
    val native: String,
    val region: String,
    val region_id: Int,
    val subregion: String,
    val subregion_id: Int,
    val nationality: String,
    val timezones: List<Timezone>,
    val translations: Map<String, String>,
    val latitude: String,
    val longitude: String,
    val emoji: String,
    val emojiU: String
)

data class Timezone(
    val zoneName: String,
    val gmtOffset: Int,
    val gmtOffsetName: String,
    val abbreviation: String,
    val tzName: String
)