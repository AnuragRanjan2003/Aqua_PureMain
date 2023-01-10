package com.example.project3

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log.e
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.project3.databinding.FragmentAreaBinding
import com.example.project3.viewModels.ApplicationViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class AreaFragment : Fragment() {
    private lateinit var binding: FragmentAreaBinding
    private lateinit var appViewModel : ApplicationViewModel
    // TODO: Rename and change types of parameters


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAreaBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        appViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(requireActivity().application))[ApplicationViewModel::class.java]

        prepLocationUpdates()
        appViewModel.startLocationUpdates()
        appViewModel.getLocationLiveData().observe(viewLifecycleOwner){ e("loc","$it")}
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_areaFragment_to_imageFragment)
        }
        return binding.root
    }

    private fun prepLocationUpdates() {
        if(ContextCompat.checkSelfPermission(activity as AppCompatActivity,Manifest.permission.ACCESS_FINE_LOCATION)==PERMISSION_GRANTED)
            requestLocationUpdates()
        else
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestLocationUpdates() {
        appViewModel.startLocationUpdates()
    }
    private val requestSinglePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted->
        if(isGranted){
            requestLocationUpdates()
        }else{
            Toast.makeText(activity,"no gps",Toast.LENGTH_LONG).show()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AreaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AreaFragment().apply {

            }
    }
}