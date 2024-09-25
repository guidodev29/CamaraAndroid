package com.api.ej_parcial2_hg100721

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var nextButton: Button
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los elementos de la UI
        val welcomeTextView: TextView = findViewById(R.id.text_welcome)
        val cameraButton: Button = findViewById(R.id.button_camera)
        val locationButton: Button = findViewById(R.id.button_location)
        val storageButton: Button = findViewById(R.id.button_storage)
        statusTextView = findViewById(R.id.text_permission_status)
        nextButton = findViewById(R.id.button_next)

        // Solicitar permisos al hacer clic en los botones
        cameraButton.setOnClickListener {
            requestPermission(Manifest.permission.CAMERA, "Cámara")
        }
        locationButton.setOnClickListener {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, "Ubicación")
        }
        storageButton.setOnClickListener {
            requestStoragePermission()
        }

        // Manejar el clic en el botón "Siguiente"
        nextButton.setOnClickListener {
            if (allPermissionsGranted()) {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Debes otorgar todos los permisos para continuar", Toast.LENGTH_SHORT).show()
            }
        }

        // Mostrar toast si se toca el botón deshabilitado
        nextButton.setOnLongClickListener {
            if (!nextButton.isEnabled) {
                Toast.makeText(this, "Por favor, otorga todos los permisos para continuar", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // Verificar el estado de los permisos al iniciar
        updatePermissionStatus()
    }

    private fun requestPermission(permission: String, name: String) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            showToast("$name: Permiso concedido")
            updatePermissionStatus()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 101)
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                showToast("Almacenamiento: Permiso concedido")
                updatePermissionStatus()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + applicationContext.packageName)
                startActivityForResult(intent, 102)
            }
        } else {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "Almacenamiento")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Permiso concedido")
                updatePermissionStatus()
            } else {
                showToast("Permiso denegado")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    showToast("Almacenamiento: Permiso concedido")
                } else {
                    showToast("Almacenamiento: Permiso denegado")
                }
                updatePermissionStatus()
            }
        }
    }

    private fun updatePermissionStatus() {
        val cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val locationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val storageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        val statusText = "Cámara: ${if (cameraGranted) "Concedido" else "Denegado"}\n" +
                "Ubicación: ${if (locationGranted) "Concedido" else "Denegado"}\n" +
                "Almacenamiento: ${if (storageGranted) "Concedido" else "Denegado"}"

        statusTextView.text = statusText
        enableNextButtonIfPermissionsGranted()
    }

    private fun enableNextButtonIfPermissionsGranted() {
        nextButton.isEnabled = allPermissionsGranted()
    }

    private fun allPermissionsGranted(): Boolean {
        val cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val locationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val storageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        return cameraGranted && locationGranted && storageGranted
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
