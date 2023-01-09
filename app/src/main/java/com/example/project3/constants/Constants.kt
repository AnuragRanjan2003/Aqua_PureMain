package com.example.project3.constants

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Constants {
    const val WebClientId = "881280766364-no4hm5nhcrk1tfop697nmg10qdnt9sjv.apps.googleusercontent.com"
     val userRef = Firebase.firestore.collection("users")
}