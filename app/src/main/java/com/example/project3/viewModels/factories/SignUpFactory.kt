package com.example.project3.viewModels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.project3.repo.Repository
import com.example.project3.viewModels.SignUpFragmentViewModel

class SignUpFactory(private val repo :Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignUpFragmentViewModel(repo) as T
    }
}