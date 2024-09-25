package com.api.ej_parcial2_hg100721

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

class ImageAdapter(private val context: Context, private val images: ArrayList<File>) : BaseAdapter() {

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_image, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val imageFile = images[position]

        // Usar Glide para cargar la imagen en el ImageView
        Glide.with(context)
            .load(imageFile)
            .into(imageView)

        // Agregar listener de clic para mostrar la imagen en pantalla completa
        imageView.setOnClickListener {
            val intent = Intent(context, FullscreenImageActivity::class.java)
            intent.putExtra("imagePath", imageFile.absolutePath)
            context.startActivity(intent)
        }

        return view
    }
}
