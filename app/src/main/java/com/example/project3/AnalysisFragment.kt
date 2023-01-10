package com.example.project3

import android.os.Bundle
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.project3.viewModels.AnalysisFragmentViewModel


class AnalysisFragment : Fragment() {

    private val args: AnalysisFragmentArgs by navArgs()
    private val viewModel: AnalysisFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        e(tag + "url", args.imageUrl)
        viewModel.getResponse(args.imageUrl)
        viewModel.observeResponse().observe(viewLifecycleOwner) { e(tag, "response: $it") }
        return inflater.inflate(R.layout.fragment_analysis, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnalysisFragment()
    }
}