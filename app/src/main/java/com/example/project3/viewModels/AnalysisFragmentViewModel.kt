package com.example.project3.viewModels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log.e
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.project3.Completion
import com.example.project3.constants.Constants.ALGAE_LIMIT
import com.example.project3.constants.Constants.API_MODEL
import com.example.project3.constants.Constants.API_SECRET
import com.example.project3.constants.Constants.API_USER
import com.example.project3.constants.Constants.DIRT_LIMIT
import com.example.project3.ml.ModelUnquant
import com.example.project3.models.*
import com.example.project3.models.colorApimodels.Dominant
import com.example.project3.models.colorApimodels.Response
import com.example.project3.models.interpolators.HuetoWL
import com.example.project3.models.interpolators.ProcessColor
import com.example.project3.models.interpolators.RgbtoHue
import com.example.project3.repo.ApiInstance
import com.example.project3.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class AnalysisFragmentViewModel(app: Application, repository: Repository) : AndroidViewModel(app) {

    private val response: MutableLiveData<Response> by lazy { MutableLiveData<Response>() }
    private val processedInfo: MutableLiveData<processedInfo> by lazy { MutableLiveData<processedInfo>() }
    private val prediction: MutableLiveData<FloatArray> by lazy { MutableLiveData<FloatArray>() }
    private val quality: MutableLiveData<Quality> by lazy { MutableLiveData<Quality>() }
    private val locationLiveData = LocationLiveData(app)
    private val repo = repository

    fun getLocationLiveData() = locationLiveData

    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }


    fun getResponse(imageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repo.getColor(imageUrl)
            if (response.isSuccessful && response.body() != null && response.body()?.status == "success") {
                launch(Dispatchers.Main) {
                    this@AnalysisFragmentViewModel.response.value = response.body()
                    processedInfo.value = repo.getInfo(response.body()!!)
                    quality.value=  repo.getQuality(response.body()!!.colors.dominant)
                }
            } else
                e("api error", response.errorBody().toString())
        }
    }



    fun getPrediction(context: Context,uri: Uri){
        prediction.value = repo.getPrediction(context,uri)
    }

    fun sendReport(report: Report,comp:Completion){
        repo.report(report, comp)
    }




    fun observeInfo(): LiveData<processedInfo> {
        return processedInfo
    }

    fun observeResponse(): LiveData<Response> {
        return response
    }

    fun observePrediction(): LiveData<FloatArray> {
        return prediction
    }

    fun observeQuality(): LiveData<Quality> {
        return quality
    }


}