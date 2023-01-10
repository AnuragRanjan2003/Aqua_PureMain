package com.example.project3.viewModels

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project3.Completion
import com.example.project3.databinding.FragmentImageBinding
import com.example.project3.uiComponents.ProgressButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ImageFragmentViewModel : ViewModel() {
    private val uri: MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    private lateinit var binding: FragmentImageBinding
    private lateinit var context: Context
    private lateinit var pbtn: ProgressButton
    private val fuser = Firebase.auth.currentUser!!
    private val url: MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }

    fun setUI(a: FragmentImageBinding, b: Context, c: ProgressButton) {
        binding = a
        context = b
        pbtn = c
    }


    fun setUri(uri: Uri) {
        this.uri.value = uri
    }

    fun observeUri(): LiveData<Uri> {
        return uri
    }

    fun saveImage(comp: Completion) {
        val ref = Firebase.storage.getReference(
            "posts/" + fuser.uid + "/" + getName() + "." + getFileExtension(uri.value!!)
        )
        viewModelScope.launch(Dispatchers.IO) {
            val x = async { ref.putFile(uri.value!!) }
            val y = async { ref.downloadUrl.result }
            withContext(Dispatchers.Main) {
                val task = x.await()
                if (task.isSuccessful) {
                    url.value = y.await()
                    comp.onComplete()
                } else {
                    comp.onCancelled("image",task.exception?.message.toString())
                }
            }
        }

    }

    private fun getFileExtension(uri: Uri): String? {
        val cr = context.contentResolver
        val map = MimeTypeMap.getSingleton()
        return map.getExtensionFromMimeType(cr?.getType(uri))
    }

    private fun getName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.now().format(formatter)
    }

    fun getImageUrl(): LiveData<Uri> {
        return url
    }


}