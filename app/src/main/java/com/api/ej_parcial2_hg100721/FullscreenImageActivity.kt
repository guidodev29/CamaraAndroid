package com.api.ej_parcial2_hg100721

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.io.File

class FullscreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        val imageView: ImageView = findViewById(R.id.fullscreen_image_view)

        // Obtener la ruta de la imagen desde el intent
        val imagePath = intent.getStringExtra("imagePath")
        if (imagePath != null) {
            val imageFile = File(imagePath)

            // Cargar la imagen en pantalla completa usando Glide
            Glide.with(this)
                .load(imageFile)
                .into(imageView)
        }
    }
}