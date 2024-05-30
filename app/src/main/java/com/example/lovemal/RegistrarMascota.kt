package com.example.lovemal

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.lovemal.MenuActivity
import com.example.lovemal.R
import com.example.lovemal.databinding.ActivityRegistrarMascotaBinding
import com.example.lovemal.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.UUID
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class RegistrarMascota : AppCompatActivity() {

    lateinit var photo_pet: ImageView
    lateinit var btn_add : Button

    private lateinit var currentUserUid: String
    private lateinit var binding: ActivityRegistrarMascotaBinding

    private val PATH_PETS = "pets/"

    private val REQUEST_CAMERA_PERMISSION = 104

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private val almacenamiento = Firebase.storage
    private val almacenamientoRef = almacenamiento.reference

    lateinit var storageReference: StorageReference

    private var imagen: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageReference = FirebaseStorage.getInstance().reference
        photo_pet = findViewById(R.id.imgRegisDog)
        btn_add = findViewById(R.id.btn_photo)

        currentUserUid = intent.getStringExtra("currentUserUid")!!

        btn_add.setOnClickListener {
            takePictureFromCamera()
        }

        addPet()

    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { bitmap ->
                handleImageCapture(bitmap)
            }
        }
    }

    private fun handleImageCapture(imageBitmap: Bitmap) {

        val imageView = findViewById<ImageView>(R.id.imgRegisDog)
        imageView.setImageBitmap(imageBitmap)
        imagen = imageBitmap

    }

    private fun takePictureFromCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(cameraIntent)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    private fun saveImageToFirebase(imageBitmap: Bitmap, uid: String) {
        val profileImageRef = almacenamientoRef.child("profileImages/${uid}")

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = profileImageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            Toast.makeText(this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        // Validar nombre
        val name = binding.txtNombre.text.toString()
        if (TextUtils.isEmpty(name)) {
            binding.txtNombre.error = "Required."
            valid = false
        } else {
            binding.txtNombre.error = null
        }

        // Validar descripción
        val descripcion = binding.txtDescripcion.text.toString()
        if (TextUtils.isEmpty(descripcion)) {
            binding.txtDescripcion.error = "Required."
            valid = false
        } else {
            binding.txtDescripcion.error = null
        }

        // Validar raza
        val raza = binding.txtRaza.text.toString()
        if (TextUtils.isEmpty(raza)) {
            binding.txtRaza.error = "Required."
            valid = false
        } else {
            binding.txtRaza.error = null
        }

        // Validar edad
        val edad = binding.txtEdad.text.toString()
        if (TextUtils.isEmpty(edad)) {
            binding.txtEdad.error = "Required."
            valid = false
        } else {
            binding.txtEdad.error = null
        }

        // Validar altura
        val altura = binding.txtAltura.text.toString()
        if (TextUtils.isEmpty(altura)) {
            binding.txtAltura.error = "Required."
            valid = false
        } else {
            binding.txtAltura.error = null
        }

        // Validar peso
        val peso = binding.txtPeso.text.toString()
        if (TextUtils.isEmpty(peso)) {
            binding.txtPeso.error = "Required."
            valid = false
        } else {
            binding.txtPeso.error = null
        }

        // Validar selección del RadioGroup de animal
        if (binding.radioGroupMascota.checkedRadioButtonId == -1) {
            binding.radioGroupError.visibility = View.VISIBLE
            valid = false
        } else {
            binding.radioGroupError.visibility = View.GONE
        }

        // Validar selección del RadioGroup de sexo
        if (binding.radioGroupMascota2.checkedRadioButtonId == -1) {
            binding.radioGroupError2.visibility = View.VISIBLE
            valid = false
        } else {
            binding.radioGroupError2.visibility = View.GONE
        }

        return valid
    }

    private fun addPet(){
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(PATH_PETS)
        binding.btnNextStep.setOnClickListener {
            if (validateForm()) {
                val myPet = Pet()
                myPet.nombre = binding.txtNombre.text.toString()
                myPet.descripcion = binding.txtDescripcion.text.toString()
                myPet.altura = binding.txtAltura.text.toString().toInt()
                myPet.edad = binding.txtEdad.text.toString().toInt()
                myPet.raza = binding.txtRaza.text.toString()
                myPet.keyUser = currentUserUid
                myPet.peso = binding.txtPeso.text.toString().toInt()
                myPet.key = UUID.randomUUID().toString()
                myPet.animal = when (binding.radioGroupMascota.checkedRadioButtonId) {
                    R.id.radioPerro -> "Perro"
                    R.id.radioGato -> "Gato"
                    else -> ""
                }
                myPet.sexo = when (binding.radioGroupMascota2.checkedRadioButtonId){
                    R.id.radioMacho -> "Macho"
                    R.id.radioHembra -> "Hembra"
                    else -> ""
                }
                myRef = database.getReference(PATH_PETS + myPet.key)
                myRef.setValue(myPet)

                saveImageToFirebase(imagen!!, myPet.key)

                val intent = Intent(this, MenuActivity::class.java).apply {
                    putExtra("currentUserUid", currentUserUid)
                }
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}