package com.example.project3.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.project3.models.LocationLiveData

class ApplicationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationLiveData = LocationLiveData(application)

    fun getLocationLiveData() = locationLiveData

    fun startLocationUpdates(){
        locationLiveData.startLocationUpdates()
    }
}