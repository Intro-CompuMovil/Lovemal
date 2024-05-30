package com.example.lovemal

import PetManagerFirebase
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lovemal.adapter.adapterPets
import com.example.lovemal.models.MyUser
import com.example.lovemal.models.Pet
import com.google.firebase.database.*

class Perfil : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private val CAMERA_REQUEST_CODE = 101

    private lateinit var currentUserUid: String

    private val PATH_USERS = "users/"
    private val PATH_PETS = "pets/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private lateinit var petManager: PetManagerFirebase
    private lateinit var petList: MutableList<Pet>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        currentUserUid = intent.getStringExtra("currentUserUid")!!

        database = FirebaseDatabase.getInstance()
        petManager = PetManagerFirebase()

        getUserFromDB()

        val btnNewPet = findViewById<ImageButton>(R.id.btnAddPet)
        btnNewPet.setOnClickListener { addPet() }

        askPermissionCamera()

        // Cargar la lista de mascotas usando el callback
        petManager.loadPets(object : PetManagerFirebase.PetLoadCallback {
            override fun onPetsLoaded(pets: List<Pet>) {
                petList = pets.toMutableList()
                fillList()
            }
        })
    }

    private fun getUserFromDB() {
        myRef = database.getReference(PATH_USERS)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myUser = singleSnapshot.getValue(MyUser::class.java)
                    Log.i(ContentValues.TAG, "Encontró usuario: " + myUser?.name)
                    if (myUser?.key == currentUserUid) {
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

    private fun addPet() {
        val intent = Intent(this, RegistrarMascota::class.java).apply {
            putExtra("currentUserUid", currentUserUid)
        }
        startActivity(intent)
    }

    private fun fillList() {
        // Usar el adaptador personalizado adapterPets
        val adapter = adapterPets(this, petList, currentUserUid)

        // Obtener la referencia del ListView
        val listView = findViewById<ListView>(R.id.petList)

        // Asignar el adaptador al ListView
        listView.adapter = adapter
        
        listView.setOnItemClickListener { parent, view, position, id ->
            makeAllFalse()

            val mascotaElegida = petList[position]

            database = FirebaseDatabase.getInstance()
            myRef = database.getReference(PATH_PETS)

            val petRef = myRef.child(mascotaElegida.key)

            petRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // El objeto existe, actualizar el valor
                        val pet = dataSnapshot.getValue(Pet::class.java)
                        pet?.let {
                            it.aprobado = true // Modificar el valor deseado
                            petRef.setValue(it) // Guardar los cambios en la base de datos
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }
    private fun makeAllFalse(){
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val pet = snapshot.getValue(Pet::class.java)
                        pet?.let {
                            // Si el keyUser coincide con el currentUserUid, cambiar aprobado a false
                            if (it.keyUser == currentUserUid) {
                                it.aprobado = false
                                // Guardar los cambios en la base de datos
                                snapshot.ref.setValue(it)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejo de errores
                Log.e("FirebaseError", "Error: ${databaseError.message}")
            }
        })
    }

    private fun askPermissionCamera() {
        val btnCamara = findViewById<ImageButton>(R.id.btnEditarFoto)
        btnCamara.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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