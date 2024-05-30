package com.example.lovemal

import android.annotation.SuppressLint
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

    private lateinit var btnDislike: ImageButton
    private lateinit var btnLike: ImageButton
    private lateinit var petsList: MutableList<Pet>
    private lateinit var currentUserUid: String
    private val PATH_PETS = "pets/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private lateinit var raza: String
    private lateinit var animal: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portal)

        currentUserUid = intent.getStringExtra("currentUserUid")!!

        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(PATH_PETS)

        loadPets()

        manageButtons()
    }

    private fun loadPets(){
        petsList = mutableListOf()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myPuppy = singleSnapshot.getValue(Pet::class.java)
                    Log.i(ContentValues.TAG, "Encontró mascota: " + myPuppy?.nombre)
                    if (myPuppy != null) {
                        petsList.add(myPuppy)
                    }
                }
                updateDisplayedInfo()
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
            val intent = Intent(this, Match::class.java).apply {
                putExtra("petUid", petsList[0].key)
            }
            startActivity(intent)
        }

        btnDislike.setOnClickListener {
            // Mover el primer perro al final de la lista
            val firstPet = petsList.removeAt(0)
            petsList.add(firstPet)
            // Actualizar la información y la imagen mostrada
            updateDisplayedInfo()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDisplayedInfo() {
        for(pet: Pet in petsList){
            if(pet.keyUser == currentUserUid){
                if(pet.aprobado){
                    raza = pet.raza
                    animal = pet.animal
                }
            }
        }
        if (petsList.isNotEmpty()) {
            val NombrePet = findViewById<TextView>(R.id.txtNombrePerro)
            val edadPet = findViewById<TextView>(R.id.txtEdadpet)
            val breedPet = findViewById<TextView>(R.id.txtbreed)
            val InfPet = findViewById<TextView>(R.id.txtInfPet)

            val currentPet = petsList[0]

            if(currentPet.raza == raza && currentPet.animal == animal && currentPet.keyUser != currentUserUid) {
                NombrePet.text = currentPet.nombre
                edadPet.text = "Edad: ${currentPet.edad} años"
                breedPet.text = "Raza: ${currentPet.raza}"
                InfPet.text = currentPet.descripcion
            }
            else{
                val firstPet = petsList.removeAt(0)
                petsList.add(firstPet)
            }
        }
    }
}