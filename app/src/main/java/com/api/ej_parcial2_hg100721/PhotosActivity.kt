package com.api.ej_parcial2_hg100721

import android.os.Bundle
import android.os.Environment
import android.widget.GridView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class PhotosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        val gridView: GridView = findViewById(R.id.gridView)
        val images = getImagesFromStorage()
        val adapter = ImageAdapter(this, images)
        gridView.adapter = adapter
    }

    // Obtener las imágenes guardadas en el almacenamiento
    private fun getImagesFromStorage(): ArrayList<File> {
        val images = ArrayList<File>()
        val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (directory?.exists() == true) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        images.add(file)
                    }
                }
            }
        }
        return images
    }
}