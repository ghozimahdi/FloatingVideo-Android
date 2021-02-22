package com.example.youtubelikewa.ui

import androidx.appcompat.app.AppCompatActivity

object FlyingVideo {
    private var taskCoffeeVideo: GUIVideo? = null
    operator fun get(activity: AppCompatActivity): GUIVideo? {
        if (taskCoffeeVideo == null) {
            taskCoffeeVideo = GUIVideo.getInstance(activity)
        }
        return taskCoffeeVideo
    }
}