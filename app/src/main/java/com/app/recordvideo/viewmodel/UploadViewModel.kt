package com.app.recordvideo.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.recordvideo.Utils.MyApplication
import com.app.recordvideo.Utils.Resource
import com.app.recordvideo.models.UploadFileResponse
import com.app.recordvideo.repository.UploadRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.IOException

class UploadViewModel(app: Application,
                      val uploadRepository: UploadRepository ) : AndroidViewModel(app){ //inheriting from android view model to use application context
    //here we use application context to get the context throughout the app running,
    //so it will work even if the activity changes or destroys, the app context will still work until the app's running

    //LIVEDATA OBJECT
    val uploadfile: MutableLiveData<Resource<UploadFileResponse>> = MutableLiveData()

    var uploadresponse : UploadFileResponse? = null






    fun uploadFile(token: String,file: MultipartBody.Part)= viewModelScope.launch {
        uploadimage(token,file)

    }



    private suspend fun uploadimage(token: String,file:MultipartBody.Part){
        uploadfile.postValue(Resource.Loading())
        try{
            if (hasInternetConnection()){
                val response= uploadRepository.uplodafile(token, file)
                //handling response
                uploadfile.postValue(handleuploadimageresponse(response))
            }else{
                uploadfile.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable){
            when(t){
                is IOException -> uploadfile.postValue(Resource.Error("Network Failure"))
                else-> uploadfile.postValue(Resource.Error("Conversion Error"))
            }
        }
    }



    private fun handleuploadimageresponse(response: Response<UploadFileResponse>): Resource<UploadFileResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->

                return  Resource.Success(uploadresponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }





    private fun hasInternetConnection(): Boolean{
        val connectivityManager= getApplication<MyApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork= connectivityManager.activeNetwork?: return false
        val capabilities= connectivityManager.getNetworkCapabilities(activeNetwork)?: return false

        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)-> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
            else -> false
        }
    }

}