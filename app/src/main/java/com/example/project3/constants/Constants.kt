package com.example.project3.constants

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object Constants {
    const val WebClientId =
        "881280766364-no4hm5nhcrk1tfop697nmg10qdnt9sjv.apps.googleusercontent.com"
    val userRef = Firebase.firestore.collection("users")
    const val FILE_NAME = "photo.jpg"
    const val authority = "com.example.fileprovider2"
    const val GET_URl = "check.json"
    const val BASE_URL = "https://api.sightengine.com/1.0/"
    const val API_USER = "432909989"
    const val API_SECRET = "ygsuZ7ipGQuyDrYFLAqU"
    const val ALGAE_LIMIT = 0.00f
    const val DIRT_LIMIT = 0.00f
    const val API_MODEL= "properties"
    const val GOOD = 0.95
    const val MID =0.9
    const val OK = "drinkable"
    const val RISKY = "risky"
    const val BAD = "unfit"
}