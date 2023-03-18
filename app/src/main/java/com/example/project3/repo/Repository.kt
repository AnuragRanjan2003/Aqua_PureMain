package com.example.project3.repo

import android.content.AbstractThreadedSyncAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.project3.Completion
import com.example.project3.adapters.AreaAdapter
import com.example.project3.constants.Constants
import com.example.project3.databinding.FragmentLoginBinding
import com.example.project3.databinding.FragmentSignUpBinding
import com.example.project3.ml.ModelUnquant
import com.example.project3.models.Quality
import com.example.project3.models.Report
import com.example.project3.models.User
import com.example.project3.models.colorApimodels.Dominant
import com.example.project3.models.colorApimodels.Response
import com.example.project3.models.helpers.Formatters
import com.example.project3.models.interpolators.HuetoWL
import com.example.project3.models.interpolators.ProcessColor
import com.example.project3.models.interpolators.RgbtoHue
import com.example.project3.models.processedInfo
import com.example.project3.uiComponents.ProgressButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Repository {
    private val fuser = Firebase.auth.currentUser
    private val formatter = Formatters()
    suspend fun getColor(
        url: String,
        model: String = Constants.API_MODEL,
        apiUser: String = Constants.API_USER,
        apiSecret: String = Constants.API_SECRET
    ): retrofit2.Response<Response> {
        return ApiInstance.api.getResults(url, model, apiUser, apiSecret)

    }

    fun getInfo(response: Response): processedInfo {
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
        Log.e("dhue", dhue.toString())
        Log.e("chue", chue.toString())
        val dw = HuetoWL(dhue).computeWl()
        val cdw = HuetoWL(chue).computeWl()
        return processedInfo(dw, cdw, 0.00, 0.00)

    }

    fun getQuality(color: Dominant): Quality {
        val g = ProcessColor(color).computeGreen()
        val b = ProcessColor(color).computeBrown()
        var algae = 0.00f
        var dirt = 0f
        if (g >= Constants.ALGAE_LIMIT) algae = g - Constants.ALGAE_LIMIT
        if (b >= Constants.DIRT_LIMIT) dirt = b - Constants.DIRT_LIMIT
        return Quality(0f, algae, dirt)
    }

    fun getPrediction(context: Context, uri: Uri): FloatArray? {
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

        return outputFeature0.floatArray
    }

    fun saveImage(comp: Completion, uri: Uri, context: Context) {
        val ref = Firebase.storage.getReference(
            "posts/" + fuser!!.uid + "/" + formatter.getName() + "." + formatter.getFileExtension(
                uri,
                context
            )
        )
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener {
                        comp.onComplete(it.toString())
                    }
                    .addOnFailureListener {
                        comp.onCancelled("data", it.message.toString())
                    }
            }
            .addOnFailureListener {
                comp.onCancelled("data", it.message.toString())
            }
    }

    fun signIn(email:String,pass: String,binding: FragmentLoginBinding,progressButton: ProgressButton,comp: Completion){
        Firebase.auth
            .signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                comp.onComplete()
                progressButton.deactivate()
            }
            .addOnFailureListener {
                Snackbar.make(binding.root,it.message.toString(), Snackbar.LENGTH_LONG).show()
                comp.onCancelled("login Error", it.message.toString())
                progressButton.deactivate()
            }
    }

     fun saveUserData(
        fUser: FirebaseUser,
        comp: Completion,
        binding: FragmentLoginBinding
    ) {
        val user = User(fUser.email!!, fUser.uid)
        Constants.userRef
            .document(fUser.uid)
            .set(user)
            .addOnSuccessListener {
                comp.onComplete()
            }
            .addOnFailureListener {
                Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_LONG).show()
                comp.onCancelled("database", it.message.toString())
            }


    }

    fun authSaveUserData(it : AuthResult,comp: Completion,email:String,pass: String,button: ProgressButton,binding: FragmentSignUpBinding){
            Constants.userRef
                .document(it.user!!.uid)
                .set(User(email, it.user!!.uid))
                .addOnSuccessListener {
                    button.deactivate()
                    comp.onComplete()
                }
                .addOnFailureListener {
                    button.deactivate()
                    Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    comp.onCancelled("database", it.message.toString())
                }


    }

    fun report(report : Report,comp: Completion){
        val ref = Firebase.database.getReference("Reports")
        val lat = Formatters().formatToName(report.lat)
        val lon = Formatters().formatToName(report.lon)

        ref
            .child(lat)
            .child(lon)
            .child(report.uid+" "+dateTime())
            .setValue(report)
            .addOnSuccessListener { comp.onComplete("") }
            .addOnFailureListener { comp.onCancelled("report",it.message.toString()) }
    }

    private fun dateTime():String {
        val sf = SimpleDateFormat("dd MM yyyy HH:mm:ss", Locale.getDefault())
        return sf.format(Date())
    }



}