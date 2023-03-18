package com.example.project3

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.nfc.FormatException
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project3.adapters.AreaAdapter
import com.example.project3.databinding.FragmentAreaBinding
import com.example.project3.models.Quality
import com.example.project3.models.Report
import com.example.project3.models.helpers.WrapContentLinearLayoutManager
import com.example.project3.viewModels.ApplicationViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class AreaFragment : Fragment() {
    private lateinit var binding: FragmentAreaBinding
    private lateinit var appViewModel: ApplicationViewModel
    private lateinit var adapter: AreaAdapter
    private var lat = "0"
    private var lon = "0"
    private var list = ArrayList<Report>()
    private val AREA_0 = 12391.88
    private var limit = 0.00
    private var Var = 0.00
    // TODO: Rename and change types of parameters


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAreaBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        appViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[ApplicationViewModel::class.java]

        prepLocationUpdates()
        appViewModel.startLocationUpdates()
        appViewModel.getLocationLiveData().observe(viewLifecycleOwner) {
            e("loc", "$it")
            lat = it.latitude.toDouble().toInt().toString()
            lon = it.longitude.toDouble().toInt().toString()
            e("lat", lat)
            e("lon", lon)
            loadData()
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_areaFragment_to_imageFragment)
        }
        binding.areaRec.layoutManager = WrapContentLinearLayoutManager(
            requireActivity().applicationContext,
            LinearLayoutManager.VERTICAL
        )
        binding.areaRec.hasFixedSize()
        adapter = AreaAdapter(list)
        binding.areaRec.adapter = adapter

        return binding.root
    }

    private fun prepLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                activity as AppCompatActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED
        )
            requestLocationUpdates()
        else
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestLocationUpdates() {
        appViewModel.startLocationUpdates()
    }

    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestLocationUpdates()
            } else {
                Toast.makeText(activity, "no gps", Toast.LENGTH_LONG).show()
            }
        }

    private fun loadData() {
        val ref = Firebase.database.getReference("Reports").child(lat).child(lon)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dateNow = LocalDate.now()
                adapter.clear()
                for (report in snapshot.children) {
                    report.getValue(Report::class.java)?.apply {
                        val date = SimpleDateFormat(
                            "dd-MM-yyyy",
                            Locale.getDefault()
                        ).parse(this.date)
                        e("date", "${date.month + 1}+${date.year + 1900}")
                        if (dateNow.year == date.year + 1900 && dateNow.monthValue == date.month + 1)
                            adapter.addItem(this)
                    }
                }
                e("list", "${adapter.list}")
                if (adapter.list.isNotEmpty())
                    processQuality(
                        adapter.list,
                        lat.toDouble().toInt()
                    )

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



    }


    private fun processQuality(reportList: MutableList<Report>?, lat: Int) {
        val area = AREA_0 * cos(toRad(lat))
        var qualInd = 0.00
        var algae = 0.00
        var dirty = 0.00
        var sd = 0.00
        if (!reportList.isNullOrEmpty()) {
            val n = reportList.size
            for (report in reportList) {
                qualInd += report.drinkable
                algae += report.algae
                dirty += report.dirty
                sd += report.drinkable * report.drinkable
            }
            qualInd /= n
            algae /= n
            dirty /= n
            sd /= n
            Var -= qualInd * qualInd
            Var = sqrt(Var / n)
            qualInd = (qualInd * 10000 / area)
            algae = (algae / area)
            dirty = (dirty / area)


            val quality = Quality(qualInd.toFloat(), algae.toFloat(), dirty.toFloat())
            e("ind", "$qualInd")
            limit = 0.95 - 0.1554 * Var
            putValues(quality)
        }
    }

    private fun getStatus(quality: Quality): String {
        return if (quality.qualInd!! / 10000 > limit) "Good"
        else "Not Good"

    }

    private fun putValues(quality: Quality) {

        val bg = when (getStatus(quality)) {
            "Good" -> requireActivity().resources.getDrawable(R.drawable.good_water_grad, null)
            else -> requireActivity().resources.getDrawable(R.drawable.bad_water_grad, null)
        }
        binding.cardBg.background = bg
        binding.cardPlaceholder.visibility = View.INVISIBLE
        binding.mainCard.visibility = View.VISIBLE
        try {
            binding.index.text = String.format("%.2f", quality.qualInd)
        } catch (e: FormatException) {
            e("formatting error", e.message.toString())
        }
        binding.cases.text = adapter.list.size.toString()
    }

    private fun toRad(deg: Int): Double {
        return PI * deg / 180
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