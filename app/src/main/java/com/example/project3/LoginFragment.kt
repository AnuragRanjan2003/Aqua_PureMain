package com.example.project3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.animatediconbutton.AnimatedButton
import com.example.project3.databinding.FragmentLoginBinding
import com.example.project3.repo.Repository
import com.example.project3.uiComponents.ProgressButton
import com.example.project3.viewModels.LoginFragmentViewModel
import com.example.project3.viewModels.factories.LoginFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginFragmentViewModel
    private lateinit var gButton: AnimatedButton
    private lateinit var button: View
    private lateinit var pBtn: ProgressButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val repo = Repository()
        val factory = LoginFactory(repo)
        viewModel = ViewModelProvider(this, factory)[LoginFragmentViewModel::class.java]

        checkUser()
        gButton = binding.animatedButton
        gButton.changeText = "please wait.."
        gButton.setPadding(30, 10, 30, 10)
        button = binding.root.findViewById(R.id.include)
        pBtn = ProgressButton(
            text = "Login",
            iconEnabled = false,
            changeText = "please wait..",
            view = button,
            icon = requireActivity().resources.getDrawable(R.drawable.google_logo, null)
        )
        viewModel.setUi(binding, gButton, pBtn)




        binding.email.doAfterTextChanged { viewModel.email.value = it.toString() }
        binding.pass.doAfterTextChanged { viewModel.pass.value = it.toString() }

        viewModel.getPass().observe(viewLifecycleOwner) {
            passwordUI(it)
        }

        button.setOnClickListener {
            pBtn.activate()
            viewModel.login(compLogin)
        }

        gButton.setOnClickListener {
            gButton.activateButton()
            val intent = viewModel.loginWithGoogle(activity as AppCompatActivity)
            startActivityForResult(intent, 101)
        }





        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        return binding.root
    }

    private fun checkUser() {
        val user = Firebase.auth.currentUser
        if (user != null)
            findNavController().navigate(R.id.action_loginFragment_to_areaFragment)
    }

    private val compLogin = object : Completion {
        override fun onComplete(url: String) {
            findNavController().navigate(R.id.action_loginFragment_to_areaFragment)
        }

        override fun onCancelled(name: String, message: String) {
            e(name, message)
        }
    }

    private val comp2 = object : Completion {
        override fun onComplete(url: String) {
            findNavController().navigate(R.id.action_loginFragment_to_areaFragment)
        }

        override fun onCancelled(name: String, message: String) {
            e(name, message)
        }
    }


    private fun passwordUI(it: String) {
        if (it.trim().length >= 6) {
            binding.textInputLayout.counterTextColor =
                requireActivity().resources.getColorStateList(R.color.green, null)
            binding.textInputLayout.boxStrokeColor =
                requireActivity().resources.getColor(R.color.primary_light, null)
            binding.textInputLayout.hintTextColor =
                requireActivity().resources.getColorStateList(R.color.primary_light, null)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            viewModel.handleResult(task, activity as AppCompatActivity, comp2)
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {

            }
    }
}