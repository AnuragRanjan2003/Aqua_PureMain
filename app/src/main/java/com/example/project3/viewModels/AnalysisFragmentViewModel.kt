package com.example.project3.viewModels

import android.util.Log.e
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project3.constants.Constants.ALGAE_LIMIT
import com.example.project3.constants.Constants.API_MODEL
import com.example.project3.constants.Constants.API_SECRET
import com.example.project3.constants.Constants.API_USER
import com.example.project3.constants.Constants.DIRT_LIMIT
import com.example.project3.models.Quality
import com.example.project3.models.colorApimodels.Dominant
import com.example.project3.models.colorApimodels.Response
import com.example.project3.models.interpolators.HuetoWL
import com.example.project3.models.interpolators.ProcessColor
import com.example.project3.models.interpolators.RgbtoHue
import com.example.project3.models.processedInfo
import com.example.project3.repo.ApiInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnalysisFragmentViewModel : ViewModel() {

    private val response: MutableLiveData<Response> by lazy { MutableLiveData<Response>() }
    private val processedInfo: MutableLiveData<processedInfo> by lazy { MutableLiveData<processedInfo>() }
    private val prediction: MutableLiveData<FloatArray> by lazy { MutableLiveData<FloatArray>() }
    private val quality: MutableLiveData<Quality> by lazy { MutableLiveData<Quality>() }

    fun getResponse(imageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiInstance.api.getResults(imageUrl, API_MODEL, API_USER, API_SECRET)
            if (response.isSuccessful && response.body() != null && response.body()?.status == "success") {
                launch(Dispatchers.Main) {
                    this@AnalysisFragmentViewModel.response.value = response.body()
                    processInfo(response.body()!!)
                    getQuality(response.body()!!.colors.dominant)

                }
            } else
                e("api error", response.errorBody().toString())
        }
    }


    fun observeResponse(): LiveData<Response> {
        return response
    }

    private fun processInfo(response: Response) {
        val dhue = RgbtoHue(
            response.colors.dominant.r,
            response.colors.dominant.g,
            response.colors.dominant.b
        ).getHue()
        val chue = RgbtoHue(
            255 - response.colors.dominant.r,
            255 - response.colors.dominant.g,
            255 - response.colors.dominant.b
        ).getHue()
        e("dhue", dhue.toString())
        e("chue", chue.toString())
        val dw = HuetoWL(dhue).computeWl()
        val cdw = HuetoWL(chue).computeWl()
        processedInfo.value = processedInfo(dw, cdw, 0.00, 0.00)
    }

    private fun getQuality(color: Dominant) {
        val g = ProcessColor(color).computeGreen()
        val b = ProcessColor(color).computeBrown()
        var algae = 0.00f
        var dirt = 0f
        if (g >= ALGAE_LIMIT) algae = g - ALGAE_LIMIT
        if (b >= DIRT_LIMIT) dirt = b - DIRT_LIMIT
        quality.value = Quality(0f, algae, dirt)
    }


}