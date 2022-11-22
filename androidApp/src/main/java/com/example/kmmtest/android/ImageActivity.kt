package com.example.kmmtest.android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import coil.load
import coil.size.Size
import coil.transition.CrossfadeTransition
import com.github.chrisbanes.photoview.PhotoView

class ImageActivity : AppCompatActivity() {

    companion object {

        private const val KEY_URL = "key_url_image_activity"

        fun startNewInstance(context: Context, url: String) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra(KEY_URL, url)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val url = intent.getStringExtra(KEY_URL)

        val photoView = findViewById<PhotoView>(R.id.photoView)

        photoView.load(url)
    }
}