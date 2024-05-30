package com.example.lovemal.adapter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lovemal.MenuActivity
import com.example.lovemal.R
import com.example.lovemal.databinding.ActivityRegistrarMascotaBinding
import com.example.lovemal.models.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID
import com.squareup.picasso.Picasso


class RegistrarMascota : AppCompatActivity() {

    lateinit var photo_pet: ImageView
    lateinit var btn_add : Button

    private lateinit var currentUserUid: String
    private lateinit var binding: ActivityRegistrarMascotaBinding

    private val PATH_PETS = "pets/"
    private val PATH_PROFILE_IMAGES = "profileImages/"
    val storage_path = "profileImages/"
    private lateinit var mfirestore: FirebaseFirestore

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    lateinit var storageReference: StorageReference

    val COD_SEL_STORAGE = 200
    val COD_SEL_IMAGE = 300

    private var imageUrl: Uri? = null
    val photo: String = "photo"
    val idd: String = ""

    private lateinit var progressDialog: ProgressDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageReference = FirebaseStorage.getInstance().reference
        photo_pet = findViewById(R.id.imgRegisDog)
        btn_add = findViewById(R.id.btn_photo)




        currentUserUid = intent.getStringExtra("currentUserUid")!!

        btn_add.setOnClickListener {
            uploadPhoto()
        }

        addPet()

    }
    private fun uploadPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        startActivityForResult(intent, COD_SEL_IMAGE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == COD_SEL_IMAGE) {
                val imageUrl: Uri? = data?.data
                imageUrl?.let {
                    subirPhoto(it)
                }
            }
        }
    }
    private fun subirPhoto(imageUrl: Uri) {
        progressDialog.setMessage("Actualizando foto")
        progressDialog.show()
        val rute_storage_photo = "$storage_path$photo${currentUserUid}$idd"
        val reference: StorageReference = storageReference.child(rute_storage_photo)
        reference.putFile(imageUrl)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                uriTask.addOnSuccessListener { uri ->
                    val downloadUri = uri.toString()
                    val map = hashMapOf<String, Any>("photo" to downloadUri)
                    mfirestore.collection("pet").document(idd).update(map)
                        .addOnSuccessListener {
                            Toast.makeText(this@RegistrarMascota, "Foto actualizada", Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@RegistrarMascota, "Error al cargar foto", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@RegistrarMascota, "Error al cargar foto", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getPet(id: String) {
        mfirestore.collection("pet").document(id).get()
            .addOnSuccessListener { documentSnapshot ->

                val photoPet = documentSnapshot.getString("photo")

                try {
                    if (!photoPet.isNullOrEmpty()) {
                        val toast = Toast.makeText(applicationContext, "Cargando foto", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.TOP, 0, 200)
                        toast.show()
                        Picasso.get()
                            .load(photoPet)
                            .resize(150, 150)
                            .into(photo_pet)
                    }
                } catch (e: Exception) {
                    Log.v("Error", "e: $e")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return false
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