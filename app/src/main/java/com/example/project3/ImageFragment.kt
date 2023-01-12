package com.example.project3

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.project3.constants.Constants.FILE_NAME
import com.example.project3.constants.Constants.authority
import com.example.project3.databinding.FragmentImageBinding
import com.example.project3.uiComponents.ProgressButton
import com.example.project3.viewModels.ImageFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File


class ImageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentImageBinding
    private val viewModel: ImageFragmentViewModel by activityViewModels()

    private lateinit var photoFile: File


    private var uri: Uri? = null
    private lateinit var pbtn: View
    private lateinit var progressButton: ProgressButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater, container, false)
        Glide.with(this).load(R.drawable.image_placeholder).into(binding.imageView)
        pbtn = binding.root.findViewById(R.id.analyze_btn)
        val icon = requireActivity().resources.getDrawable(R.drawable.google_logo, null)
        progressButton = ProgressButton(
            text = "Analyze",
            changeText = "Analyzing..",
            icon = icon,
            iconEnabled = false,
            view = pbtn
        )

        viewModel.setUI(binding, activity as AppCompatActivity, progressButton)




        binding.cameraBtn.setOnClickListener {
            askToOpenCamera()
        }

        binding.galleryBtn.setOnClickListener {
            openGallery()
        }

        viewModel.observeUri().observe(viewLifecycleOwner) {
            uri = it
            e("uri", it.toString())
            if (it != null)
                Glide.with(this)
                    .load(it)
                    .into(binding.imageView)
        }

        pbtn.setOnClickListener {
            progressButton.activate()
            if (uri == null) {
                Snackbar.make(binding.root, "No image selected", Snackbar.LENGTH_SHORT).show()
                progressButton.deactivate()
            } else {
                viewModel.saveImage(comp)
            }
        }

        viewModel.getImageUrl().observe(viewLifecycleOwner) { e("url", "$it") }




        return binding.root
    }

    private val comp = object : Completion {
        override fun onComplete(url: String) {
            progressButton.deactivate()
            val action = ImageFragmentDirections.actionImageFragmentToAnalysisFragment(url,uri.toString())
            findNavController().navigate(action)
            e(tag, "Done")
        }

        override fun onCancelled(name: String, message: String) {
            progressButton.deactivate()
            e(tag, "failed : $message")
        }
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImageFragment()
    }

    private fun askToOpenCamera() {
        Dexter
            .withContext(activity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(permissionListener)
            .check()
    }

    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
            openCamera()
        }

        override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
            Snackbar.make(binding.root, "permission needed", Snackbar.LENGTH_SHORT).show()
        }

        override fun onPermissionRationaleShouldBeShown(
            p0: PermissionRequest?,
            p1: PermissionToken?
        ) {
            p1?.continuePermissionRequest()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(FILE_NAME)

        val fileProvider = FileProvider.getUriForFile(
            requireActivity().baseContext,
            authority,
            photoFile
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        startActivityForResult(intent, 100)
    }

    private fun getPhotoFile(fileName: String): File {
        val storage = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storage)
    }


    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(BitmapFactory.decodeFile(photoFile.absolutePath))
                .into(binding.imageView)
            uri = photoFile.toUri()
            viewModel.setUri(uri!!)
        } else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(data?.data).into(binding.imageView)
            uri = data?.data
            viewModel.setUri(uri!!)
        } else super.onActivityResult(requestCode, resultCode, data)

    }


}