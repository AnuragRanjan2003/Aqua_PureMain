package com.example.project3.models.colorApimodels

data class Colors(
    val accent: List<Accent>,
    val dominant: Dominant,
    val other: List<Other>
)