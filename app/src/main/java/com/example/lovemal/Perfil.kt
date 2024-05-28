package com.example.lovemal

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Perfil : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private val CAMERA_REQUEST_CODE = 101

    private lateinit var currentUserUid: String

    private val PATH_PETS = "pets/"
    private val PATH_USERS = "users/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        currentUserUid = intent.getStringExtra("currentUserUid")!!

        getUserFromDB()

        val btnNewPet = findViewById<ImageButton>(R.id.btnAddPet)
        btnNewPet.setOnClickListener { addPet() }

        askPermissionCamera()

        fillList()
    }

    private fun getUserFromDB(){
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(PATH_USERS)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myUser = singleSnapshot.getValue(MyUser::class.java)
                    Log.i(ContentValues.TAG, "Encontró usuario: " + myUser?.name)
                    if(myUser?.key == currentUserUid){
                        val txtName = findViewById<TextView>(R.id.idName2)
                        txtName.text = myUser.name
                        val txtCorreo = findViewById<TextView>(R.id.textViewCorreo2)
                        txtCorreo.text = myUser.email
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "error en la consulta", databaseError.toException())
            }
        })
    }

    private fun addPet(){
        val intent = Intent(this, RegistrarMascota::class.java).apply {
            putExtra("currentUserUid", currentUserUid)
        }
        startActivity(intent)
    }

    private fun fillList() {
        // Obtener los arreglos de recursos XML
        val mascotasArray = resources.getStringArray(R.array.mascotas)

        // Crear un adaptador para el ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mascotasArray)

        // Obtener la referencia del ListView
        val listView = findViewById<ListView>(R.id.petList)

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
