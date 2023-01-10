package com.example.project3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.project3.databinding.FragmentSignUpBinding
import com.example.project3.uiComponents.ProgressButton
import com.example.project3.viewModels.SignUpFragmentViewModel


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpFragmentViewModel by viewModels()
    private lateinit var pBtn: ProgressButton
    private lateinit var button: View
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        button = binding.root.findViewById(R.id.include1)
        pBtn = ProgressButton(
            text = "Sign Up",
            changeText = "please wait..",
            icon = requireActivity().resources.getDrawable(R.drawable.google_logo, null),
            iconEnabled = false,
            view = button
        )

        viewModel.setUi(binding, pBtn)

        binding.email.doAfterTextChanged { viewModel.email.value = it.toString() }
        binding.pass.doAfterTextChanged { viewModel.pass.value = it.toString() }

        viewModel.getPass().observe(viewLifecycleOwner){
            passwordUI(it)
        }

        button.setOnClickListener {
            viewModel.signUp(comp)
        }

        binding.logIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }



        return binding.root
    }

    private val comp = object : Completion {
        override fun onComplete(url: String) {
            findNavController().navigate(R.id.action_signUpFragment_to_areaFragment)
        }

        override fun onCancelled(name: String, message: String) {
            TODO("Not yet implemented")
        }
    }
    private fun passwordUI(it:String){
        if (it.trim().length >= 6) {
            binding.textInputLayout.counterTextColor =
                requireActivity().resources.getColorStateList(R.color.green, null)
            binding.textInputLayout.boxStrokeColor =
                requireActivity().resources.getColor(R.color.primary_light, null)
            binding.textInputLayout.hintTextColor =
                requireActivity().resources.getColorStateList(R.color.primary_light,null)
        } else {
            binding.textInputLayout.counterTextColor =
                requireActivity().resources.getColorStateList(
                    com.google.android.material.R.color.design_default_color_error,
                    null
                )
            binding.textInputLayout.boxStrokeColor = requireActivity().resources.getColor(
                com.google.android.material.R.color.design_default_color_error,
                null
            )
            binding.textInputLayout.hintTextColor =
                requireActivity().resources.getColorStateList(
                    com.google.android.material.R.color.design_default_color_error, null
                )
        }
    }



}