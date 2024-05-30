package com.example.lovemal

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.lovemal.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Portal : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var btnDislike: ImageButton
    private lateinit var btnLike: ImageButton
    private lateinit var perros: MutableList<Perro>
    private var currentIndex = 0

    private lateinit var currentUserUid: String
    private val PATH_PETS = "pets/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private val petsList: MutableList<Pet> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portal)

        currentUserUid = intent.getStringExtra("currentUserUid")!!

        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(PATH_PETS)

        loadPets()

        // Inicializar la lista de perros y llenarla con información de perros e imágenes
        perros = mutableListOf(
            Perro("Apolo", "3 años, le gusta abrir puertas y escaparse de la casa",
                listOf(R.drawable.apolo, R.drawable.apolo2, R.drawable.apolo3)),
            Perro("Bella", "5 años, le encanta perseguir ardillas en el parque",
                listOf(R.drawable.bella1, R.drawable.bella2, R.drawable.bella3)),
            Perro("Rocky", "2 años, disfruta correr en la playa y nadar en el mar",
                listOf(R.drawable.perrocavil1, R.drawable.perrocavil2, R.drawable.perrocavil3))
        )

        viewPager = findViewById(R.id.viewPager2)
        val adapter = ImagePagerAdapter(this, perros[currentIndex].imagenes)
        viewPager.adapter = adapter

        manageButtons()
        updateDisplayedInfo()
    }

    private fun loadPets(){
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myPuppy = singleSnapshot.getValue(Pet::class.java)
                    Log.i(ContentValues.TAG, "Encontró mascota: " + myPuppy?.nombre)
                    if (myPuppy != null) {
                        petsList.add(myPuppy)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "error en la consulta", databaseError.toException())
            }
        })

    }

    private fun manageButtons(){
        btnLike = findViewById(R.id.btnLike)
        btnDislike = findViewById(R.id.btnDislike)

        btnLike.setOnClickListener {
            startActivity(Intent(this, Match::class.java))
        }

        btnDislike.setOnClickListener {
            // Mover el primer perro al final de la lista
            val firstPerro = perros.removeAt(0)
            perros.add(firstPerro)
            // Actualizar la información y la imagen mostrada
            updateDisplayedInfo()
        }
    }

    private fun updateDisplayedInfo() {
        val txtNombrePerro = findViewById<TextView>(R.id.txtNombrePerro)
        val txtInfPet = findViewById<TextView>(R.id.txtInfPet)
        // Obtener el perro actual
        val currentPerro = perros[0]
        // Mostrar la información del perro actual
        txtNombrePerro.text = currentPerro.nombre
        txtInfPet.text = currentPerro.info
        // Actualizar las imágenes en el ViewPager
        val adapter = ImagePagerAdapter(this, currentPerro.imagenes)
        viewPager.adapter = adapter
    }
}

data class Perro(val nombre: String, val info: String, val imagenes: List<Int>)