package com.example.lovemal

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.lovemal.adapter.sellAdapter
import com.example.lovemal.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MarketActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var petsList: MutableList<Pet>
    private lateinit var currentUserUid: String

    private var PATH_PUPPIES = "puppies/"
    private var raza: String? = null
    private var animal: String? = null

    private var puppiesList: MutableList<Pet> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market)

        // Inicializa la base de datos y referencia
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(PATH_PUPPIES)

        loadPets()
    }

    private fun loadPets(){
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myPuppy = singleSnapshot.getValue(Pet::class.java)
                    Log.i(ContentValues.TAG, "Encontró mascota: " + myPuppy?.nombre)
                    if (myPuppy != null && myPuppy.aprobado) {
                        puppiesList.add(myPuppy)
                    }
                }
                managePets()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "error en la consulta", databaseError.toException())
            }
        })
    }

    private fun managePets() {
        val listView = findViewById<ListView>(R.id.petList)

        if (listView != null) {
            val adapter = sellAdapter(this, puppiesList)
            listView.adapter = adapter
        } else {
            Log.e(ContentValues.TAG, "ListView es nulo. Verifica que el id sea correcto y que el layout se haya inflado.")
        }
    }
}
