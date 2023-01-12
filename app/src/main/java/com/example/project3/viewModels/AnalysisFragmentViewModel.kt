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
import com.example.project3.constants.Constants.ALGAE_LIMIT
import com.example.project3.constants.Constants.API_MODEL
import com.example.project3.constants.Constants.API_SECRET
import com.example.project3.constants.Constants.API_USER
import com.example.project3.constants.Constants.DIRT_LIMIT
import com.example.project3.ml.ModelUnquant
import com.example.project3.models.LocationLiveData
import com.example.project3.models.Quality
import com.example.project3.models.ValueModel
import com.example.project3.models.colorApimodels.Dominant
import com.example.project3.models.colorApimodels.Response
import com.example.project3.models.interpolators.HuetoWL
import com.example.project3.models.interpolators.ProcessColor
import com.example.project3.models.interpolators.RgbtoHue
import com.example.project3.models.processedInfo
import com.example.project3.repo.ApiInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class AnalysisFragmentViewModel(app: Application) : AndroidViewModel(app) {

    private val response: MutableLiveData<Response> by lazy { MutableLiveData<Response>() }
    private val processedInfo: MutableLiveData<processedInfo> by lazy { MutableLiveData<processedInfo>() }
    private val prediction: MutableLiveData<FloatArray> by lazy { MutableLiveData<FloatArray>() }
    private val quality: MutableLiveData<Quality> by lazy { MutableLiveData<Quality>() }
    private val locationLiveData = LocationLiveData(app)

    fun getLocationLiveData() = locationLiveData

    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }

    fun getResponse(imageUrl: String) {
        val list = ArrayList<ValueModel>()
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

    fun getPrediction(context: Context, uri: Uri) {
        val model = ModelUnquant.newInstance(context)

        var bitmap: Bitmap? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                bitmap = ImageDecoder.decodeBitmap(source)
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            println("Could not convert image to BitMap")
            e.printStackTrace()
        }
        bitmap = Bitmap.createScaledBitmap(bitmap!!, 224, 224, true)
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        val tImg = TensorImage(DataType.FLOAT32)
        tImg.load(bitmap)
        val byteBuffer = tImg.buffer
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        model.close()

        prediction.value = outputFeature0.floatArray
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