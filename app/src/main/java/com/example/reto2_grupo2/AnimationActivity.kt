package com.example.reto2_grupo2

import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class AnimationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)

        val imageView = findViewById<ImageView>(R.id.iv_animation)
        val animation = imageView.drawable as AnimationDrawable


        animation.start()
        val totalDuration = animation.numberOfFrames * 41L
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, totalDuration)
    }
}