package com.example.project3

interface Completion {
    fun onComplete(url:String="")

    fun onCancelled(name : String,message: String)
}