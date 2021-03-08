package com.example.youtubelikewa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.youtubelikewa.databinding.ActivityMainBinding
import com.example.youtubelikewa.ui.ex1.FlyingVideo
import com.example.youtubelikewa.ui.ex1.GUIVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val mTracker = YouTubePlayerTracker()

        binding?.btnShowVid?.setOnClickListener {
            FlyingVideo[this]
                ?.setFloatMode(GUIVideo.FLOAT_MOVE.STICKY)
                ?.setVideoStartSecond(mTracker.currentSecond)
                ?.videoSetup("k2gOsvK8XNM")
                ?.setFlyGravity(GUIVideo.FLY_GRAVITY.TOP)
                ?.show(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}