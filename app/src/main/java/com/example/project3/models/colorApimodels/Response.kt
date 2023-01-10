package com.example.project3.models.colorApimodels

data class Response(
    val brightness: Double,
    val colors: Colors,
    val contrast: Double,
    val media: Media,
    val request: Request,
    val sharpness: Double,
    val status: String
)