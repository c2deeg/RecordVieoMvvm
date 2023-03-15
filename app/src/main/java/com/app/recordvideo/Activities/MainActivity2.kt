package com.app.recordvideo.Activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.recordvideo.R
import com.app.recordvideo.Utils.Resource
import com.app.recordvideo.Utils.UploadViewModelFactory
import com.app.recordvideo.databinding.ActivityMain2Binding
import com.app.recordvideo.databinding.ActivityMainBinding
import com.app.recordvideo.repository.UploadRepository
import com.app.recordvideo.viewmodel.UploadViewModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class MainActivity2 : AppCompatActivity(), View.OnClickListener {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var frameList: List<Bitmap>
    private lateinit var handler: Handler
    private var binding: ActivityMain2Binding? = null
    lateinit var viewModel: UploadViewModel
    lateinit var bitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val repository = UploadRepository()
        val viewModelProviderFactory = UploadViewModelFactory(application, repository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(UploadViewModel::class.java)


        val videoUriString = intent.getStringExtra("VIDEO_URI")
        val videoUri = Uri.parse(videoUriString)
        Toast.makeText(this, videoUri.toString(), Toast.LENGTH_SHORT).show()


        mediaPlayer = MediaPlayer.create(this, videoUri)
        mediaPlayer.setOnPreparedListener {
            binding!!.videoView.start()
            binding?.seekBar!!.max = mediaPlayer.duration
            frameList = extractFramesFromVideo(videoUri)
        }
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(this, videoUri)
        val firstFrame = mediaMetadataRetriever.getFrameAtTime(0)
        binding?.imageView?.setImageBitmap(firstFrame)
        bitmap = firstFrame!!

        listeners()
        seekbar()


    }

    private fun listeners() {
        binding?.btnpostvideo?.setOnClickListener(this)

    }

    private fun seekbar() {
        binding?.seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                    binding?.imageView?.setImageBitmap(frameList[progress * frameList.size / mediaPlayer.duration])
                    bitmap = frameList[progress * frameList.size / mediaPlayer.duration]
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                binding?.seekBar?.progress = mediaPlayer.currentPosition
                handler.postDelayed(this, 100)
            }
        })

    }

    private fun extractFramesFromVideo(videoUri: Uri): List<Bitmap> {
        val frameList = mutableListOf<Bitmap>()

        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(this, videoUri)

        val duration = mediaPlayer.duration.toLong()
        val frameDuration = duration / 10 // Extract 10 frames from the video

        for (i in 0..9) {
            val frameTime = i * frameDuration
            val frame = mediaMetadataRetriever.getFrameAtTime(
                frameTime * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )
            frameList.add(frame!!)
        }

        return frameList
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnpostvideo -> {
                val multipart = bitmapToMultipart(bitmap, "File")
                viewModel.uploadFile("qwqwqwqwq", multipart)
                biindobservers()


            }
        }

    }

    fun bitmapToMultipart(bitmap: Bitmap, name: String): MultipartBody.Part {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val requestBody = RequestBody.create(
            "image/jpeg".toMediaTypeOrNull(),
            byteArrayOutputStream.toByteArray()
        )
        return MultipartBody.Part.createFormData(name, "File", requestBody)
    }

    private fun biindobservers() {
        viewModel.uploadfile.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    Toast.makeText(this, response.data?.statusCode.toString(), Toast.LENGTH_SHORT)
                        .show()
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()

                    hideProgressBar()


                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()


                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        binding?.ProgressBar?.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding?.ProgressBar?.visibility = View.VISIBLE
    }


}
