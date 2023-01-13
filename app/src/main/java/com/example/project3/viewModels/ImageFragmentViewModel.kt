package com.example.project3.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log.e
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project3.Completion
import com.example.project3.databinding.FragmentImageBinding
import com.example.project3.repo.Repository
import com.example.project3.uiComponents.ProgressButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ImageFragmentViewModel(repository: Repository) : ViewModel() {
    private val uri: MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    private lateinit var binding: FragmentImageBinding
    private lateinit var context: Context
    private lateinit var pbtn: ProgressButton
    private val url: MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    private val repo = repository
    private lateinit var cp: Completion
    fun setUI(a: FragmentImageBinding, b: Context, c: ProgressButton, completion: Completion) {
        binding = a
        context = b
        pbtn = c
        cp = completion

    }


    fun setUri(uri: Uri) {
        this.uri.value = uri
    }

    fun observeUri(): LiveData<Uri> {
        return uri
    }


    fun saveImage() {
        repo.saveImage(comp, uri.value!!, context)
    }


    fun getImageUrl(): LiveData<Uri> {
        return url
    }

    private val comp = object : Completion {
        override fun onComplete(url: String) {
            this@ImageFragmentViewModel.url.value = Uri.parse(url)
            e("url", url)
            cp.onComplete(url)
        }

        override fun onCancelled(name: String, message: String) {
            e(name, message)
        }
    }


}