package com.example.project3

import android.animation.LayoutTransition
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
import com.example.project3.models.helpers.Formatters
import com.example.project3.models.interpolators.ProcessColor
import com.example.project3.repo.Repository
import com.example.project3.uiComponents.ProgressButton
import com.example.project3.viewModels.AnalysisFragmentViewModel
import com.example.project3.viewModels.factories.AppViewModelFactory
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
    private lateinit var prog: ProgressButton
    private lateinit var btn: View
    private lateinit var repo: Repository
    private lateinit var formatter : Formatters

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        repo = Repository()
        formatter = Formatters()
        val factory = AppViewModelFactory(requireActivity().application, repo)

        viewModel = ViewModelProvider(
            this,
            factory
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
            adapter.addItem("dirty", formatter.format(dirty * 100))
            adapter.addItem("algae", formatter.format(algae * 100))
        }

        viewModel.observeInfo().observe(viewLifecycleOwner) {
            adapter.addItem("dominant\nwavelength", "${formatter.format(it.dw)} nm")
            adapter.addItem("complementary\nwavelength", "${formatter.format(it.cdw)} nm")
        }
        viewModel.getPrediction(activity as AppCompatActivity, uri)
        viewModel.observePrediction().observe(viewLifecycleOwner) {
            e("prediction", "${getStatus(it)}")
        }
        viewModel.startLocationUpdates()
        viewModel.getLocationLiveData().observe(viewLifecycleOwner) { e("loc", "$it") }

        initView()
        return binding.root
    }

    private fun putValue(it: Response) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.placeholder.visibility = View.GONE
        binding.imageView2.visibility = View.VISIBLE
        adapter.addItem("dominant color", ProcessColor(it.colors.dominant).getRgb())
        adapter.addItem("brightness", "${it.brightness * 100} %")
        showButton(true)
    }

    private fun initView() {
        Glide.with(this).load(args.imageUrl).into(binding.shapeableImageView)
        binding.linearLayout2.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        //binding.recyclerView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.placeholder.startShimmer()

        //btn
        btn = binding.root.findViewById(R.id.btn_report)
        prog = ProgressButton(
            text = "Report",
            changeText = "reporting",
            icon = requireActivity().resources.getDrawable(R.drawable.google_logo, null),
            iconEnabled = false,
            view = btn,
            textColor = requireActivity().resources.getColor(R.color.blue_deep, null)
        )

        adapter = AnalysisRecAdapter(list)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.hasFixedSize()
        binding.recyclerView.adapter = this.adapter
    }


    private fun getStatus(output: FloatArray): String {
        val ok = output[0]
        drinkable = ok
        return if (ok >= GOOD) OK
        else if (ok >= MID) RISKY
        else BAD
    }

    private fun showButton(x: Boolean) {
        btn.visibility = when (x) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }

}