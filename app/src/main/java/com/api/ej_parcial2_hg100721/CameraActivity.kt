package com.api.ej_parcial2_hg100721

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.media.ExifInterface
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var takePhotoButton: Button
    private lateinit var viewPhotosButton: Button
    private lateinit var photoFile: File
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        requestPermissions()

        imageView = findViewById(R.id.imageView)
        takePhotoButton = findViewById(R.id.button_take_photo)
        viewPhotosButton = findViewById(R.id.button_view_photos)

        // Botón para tomar foto
        takePhotoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        // Botón para ver fotos tomadas
        viewPhotosButton.setOnClickListener {
            val intent = Intent(this, PhotosActivity::class.java)
            startActivity(intent)
        }
    }

    // Intent para tomar foto
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                // Crea un archivo para la foto
                photoFile = createImageFile()
                val photoURI = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al crear archivo de imagen", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No se puede acceder a la cámara", Toast.LENGTH_SHORT).show()
        }
    }



    // Crear el archivo donde se guardará la foto
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Obtén la imagen desde el archivo, no desde data
            val imageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

            // Corrige la orientación y agrega la marca de agua
            val locationInfo = getCurrentLocation()
            val watermarkedBitmap = addWatermark(imageBitmap, locationInfo)

            // Guarda solo la imagen con la marca de agua
            saveImage(watermarkedBitmap)

            // Eliminar la imagen original para evitar duplicados
            if (photoFile.exists()) {
                photoFile.delete()
            }

            // Mostrar la imagen con la marca de agua
            imageView.setImageBitmap(watermarkedBitmap)
        } else {
            Toast.makeText(this, "No se tomó la foto", Toast.LENGTH_SHORT).show()
        }
    }





    // Obtener la ubicación actual
    private fun getCurrentLocation(): String {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return "Ubicación no disponible"
        }

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        return if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude

            // Usar Geocoder para obtener la dirección
            val geocoder = Geocoder(this, Locale.getDefault())
            try {
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

                if (addresses != null && addresses.isNotEmpty()) {
                    val address: Address = addresses[0]
                    val addressLine = address.getAddressLine(0) // Dirección completa

                    // Combinar toda la información que quieras mostrar
                    return "Dirección: $addressLine"
                } else {
                    return "No se pudo obtener la dirección"
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return "Error al obtener la dirección"
            }
        } else {
            "Ubicación no disponible"
        }
    }

    // Agregar marca de agua a la imagen

    private fun addWatermark(bitmap: Bitmap, watermarkText: String): Bitmap {
        // Corrige la orientación del bitmap
        val orientedBitmap = correctBitmapOrientation(bitmap, photoFile.absolutePath)

        val result = orientedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint()
        paint.color = Color.BLACK  // Color de la marca de agua
        paint.textSize = bitmap.width / 40f  // Tamaño del texto ajustado según la resolución
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.RIGHT  // Alinear el texto a la izquierda del punto x

        // Ajustar la posición horizontal y vertical de la marca de agua
        val xPos = (canvas.width * 0.75).toFloat() // Posición horizontal
        val yPos = (canvas.height / 4).toFloat()   // Posición vertical

        // Dibujar la marca de agua en la posición calculada
        canvas.drawText(watermarkText, xPos, yPos, paint)
        return result
    }

    // Método para corregir la orientación del bitmap
    private fun correctBitmapOrientation(bitmap: Bitmap, photoPath: String): Bitmap {
        val exif = ExifInterface(photoPath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val matrix = android.graphics.Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }



    // Guardar la imagen con marca de agua
    private fun saveImage(bitmap: Bitmap) {
        val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(directory, "photo_${System.currentTimeMillis()}.jpg")
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)  // Usar 100 para máxima calidad
            fos.flush()
            fos.close()
            Toast.makeText(this, "Foto guardada: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
        }
    }



    private fun requestPermissions() {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 104)
        }
    }

}
