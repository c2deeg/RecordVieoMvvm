package com.app.recordvideo.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.app.recordvideo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var videouri: Uri?=null
  private var ourequestcode:Int = 100
    var duration = 0
    private var binding: ActivityMainBinding?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val mediacollection = MediaController(this)
        mediacollection.setAnchorView(binding?.videoview)
        mediacollection.setMediaPlayer(binding?.videoview)
        binding?.videoview!!.setMediaController(mediacollection)

        binding?.videoview!!.setOnPreparedListener { mp ->
            duration = mp.duration
            binding?.seekBar?.max = duration
        }

        listeners()


    }

    private fun listeners(){
        binding?.imgnextscren?.setOnClickListener{

            if (videouri!=null){
                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("VIDEO_URI", videouri.toString())
                startActivity(intent)
            }else{
                Toast.makeText(this, "Please select or record video", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun Startvideo(view: View) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (intent.resolveActivity(packageManager)!=null){
            startActivityForResult(intent,ourequestcode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==ourequestcode&&resultCode== RESULT_OK){
             videouri = data?.data
            binding?.videoview!!.setVideoURI(videouri)
            binding?.videoview!!.start()
            binding?.seekBar?.max = binding?.videoview!!.duration
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    // Update the seek bar position with the current video playback position
                    binding?.seekBar?.progress = binding?.videoview!!.currentPosition
                    // Schedule the handler to run again after a short delay
                    handler.postDelayed(this, 100)
                }
            }, 0)




            binding?.seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val progressPercentage = seekBar?.progress?.toFloat()!! / seekBar.max
                    val currentPosition = (binding?.videoview!!.duration * progressPercentage).toLong()
                    Log.d("ssssssss",currentPosition.toString())

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })


        }
    }





    fun gallery(view: View) {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickIntent, ourequestcode)
    }
}