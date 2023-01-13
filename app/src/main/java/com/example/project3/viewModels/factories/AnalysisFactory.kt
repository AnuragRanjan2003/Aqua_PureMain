package com.example.project3.viewModels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.project3.repo.Repository
import com.example.project3.viewModels.AnalysisFragmentViewModel

class AnalysisFactory(private val app : Application, private val repository: Repository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnalysisFragmentViewModel(app,repository) as T
    }
}