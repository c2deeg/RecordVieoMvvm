package com.app.recordvideo.Utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.recordvideo.repository.UploadRepository
import com.app.recordvideo.viewmodel.UploadViewModel

class UploadViewModelFactory (

    val app: Application,
    val newsRepository: UploadRepository
) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UploadViewModel(app, newsRepository) as T
    }
}