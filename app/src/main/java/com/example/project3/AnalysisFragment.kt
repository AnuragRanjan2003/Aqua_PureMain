package com.example.project3

import android.net.Uri
import android.os.Bundle
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.project3.adapters.AnalysisRecAdapter
import com.example.project3.constants.Constants.BAD
import com.example.project3.constants.Constants.GOOD
import com.example.project3.constants.Constants.MID
import com.example.project3.constants.Constants.OK
import com.example.project3.constants.Constants.RISKY
import com.example.project3.databinding.FragmentAnalysisBinding
import com.example.project3.models.ValueModel
import com.example.project3.models.colorApimodels.Response
import com.example.project3.viewModels.AnalysisFragmentViewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.floor


class AnalysisFragment : Fragment() {

    private val args: AnalysisFragmentArgs by navArgs()
    private lateinit var viewModel: AnalysisFragmentViewModel
    private lateinit var uri: Uri
    private lateinit var binding: FragmentAnalysisBinding
    private var list = ArrayList<ValueModel>()
    private var drinkable = 0.00f
    private lateinit var adapter: AnalysisRecAdapter
    private var dirty: Double = 0.00
    private var algae: Double = 0.00


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAnalysisBinding.inflate(inflater, container, false)


        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[AnalysisFragmentViewModel::class.java]
        e(tag + "url", args.imageUrl)
        e(tag + "uri", args.imageUri)
        uri = Uri.parse(args.imageUri)
        viewModel.getResponse(args.imageUrl)
        viewModel.observeResponse().observe(viewLifecycleOwner) {
            e(tag, "response: $it")
            putValue(it)
        }
        viewModel.observeQuality().observe(viewLifecycleOwner) {
            e("qual", "$it")
            algae = it.algae!!.toDouble()
            dirty = it.dirty!!.toDouble()
            adapter.addItem("dirty", format(dirty * 100))
            adapter.addItem("algae", format(algae * 100))
        }
        viewModel.getPrediction(activity as AppCompatActivity, uri)
        viewModel.observePrediction().observe(viewLifecycleOwner) {
            e("prediction", "${getStatus(it)}") }
        viewModel.startLocationUpdates()
        viewModel.getLocationLiveData().observe(viewLifecycleOwner) { e("loc", "$it") }

        initView()
        return binding.root
    }

    private fun putValue(it: Response) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.placeholder.visibility = View.GONE
        binding.imageView2.visibility = View.VISIBLE
        adapter.addItem("dominant color", it.colors.dominant.hex)
        adapter.addItem("brightness", "${it.brightness*100} %")
    }

    private fun initView() {
        Glide.with(this).load(args.imageUrl).into(binding.shapeableImageView)

        binding.placeholder.startShimmer()

        adapter = AnalysisRecAdapter(list)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.hasFixedSize()
        binding.recyclerView.adapter = this.adapter
    }

    private fun format(num: Double): String {
        val df = DecimalFormat("##.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(num)
    }

    private fun formatToName(num: Double): String {
        return floor(num).toInt().toString()
    }

    private fun getStatus(output: FloatArray): String {
        val ok = output[0]
        drinkable = ok
        return if (ok >= GOOD) OK
        else if (ok >= MID) RISKY
        else BAD
    }

}