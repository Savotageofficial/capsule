package com.example.capsule.data.model

data class Country(
    val name: String,
    val code: String,
)
val countries = listOf(
    Country("Egypt", "+20"),
    Country("Saudi Arabia", "+966"),
    Country("UAE", "+971"),
    Country("USA", "+1")
)

