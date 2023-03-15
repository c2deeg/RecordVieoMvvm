package com.app.recordvideo.repository

import com.app.recordvideo.API.RetrofitInstance
import okhttp3.MultipartBody

class UploadRepository {
    suspend fun uplodafile(token:String,file:MultipartBody.Part)=
        RetrofitInstance.api.uploadVideo(token, file)
}