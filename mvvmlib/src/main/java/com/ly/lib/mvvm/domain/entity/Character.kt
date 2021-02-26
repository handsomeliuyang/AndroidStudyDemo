package com.ly.lib.mvvm.domain.entity

data class Character (
    val name: String,
    val species: String,
    val gender: String,
    val house: String,
    val dataOfBirth: String,
    val yearOfBirth: String,
    val ancestry: String,
    val patronus: String,
    val actor: String,
    val alive: Boolean,
    val image: String
)