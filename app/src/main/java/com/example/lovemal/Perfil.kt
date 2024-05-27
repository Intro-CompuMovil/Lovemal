package com.example.lovemal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Perfil : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private val CAMERA_REQUEST_CODE = 101

    private val PATH_PETS = "pets/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val currentUserUid = intent.getStringExtra("currentUserUid")

        val btnNewPet = findViewById<ImageButton>(R.id.btnAddPet)
        btnNewPet.setOnClickListener { addPet() }

        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val imageList = listOf(R.drawable.perrocavil1, R.drawable.perrocavil2, R.drawable.perrocavil3)
        val adapter = ImagePagerAdapter(this, imageList)
        viewPager.adapter = adapter

        askPermissionCamera()

        fillList()
    }

    private fun addPet(){

    }

    private fun fillList() {
        // Obtener los arreglos de recursos XML
        val mascotasArray = resources.getStringArray(R.array.mascotas)

        // Crear un adaptador para el ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mascotasArray)

        // Obtener la referencia del ListView
        val listView = findViewById<ListView>(R.id.petsList)

        // Asignar el adaptador al ListView
        listView.adapter = adapter
    }


    private fun askPermissionCamera(){
        val btnCamara = findViewById<ImageButton>(R.id.btnEditarFoto)
        btnCamara.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }   else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageView1 = findViewById<ImageView>(R.id.imageView1)
            imageView1.setImageBitmap(imageBitmap)
        }
    }
}
