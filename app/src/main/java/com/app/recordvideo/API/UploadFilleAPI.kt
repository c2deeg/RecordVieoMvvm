package com.app.recordvideo.API

import com.app.recordvideo.models.UploadFileResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadFilleAPI {
    @Multipart
    @POST("User/UploadFile")
    suspend fun uploadVideo( @Header("authorization")token:String, @Part images: MultipartBody.Part): Response<UploadFileResponse>
}