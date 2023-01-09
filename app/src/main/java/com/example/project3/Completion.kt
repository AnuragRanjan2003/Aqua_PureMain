package com.example.project3

interface Completion {
    fun onComplete()

    fun onCancelled(name : String,message: String)
}