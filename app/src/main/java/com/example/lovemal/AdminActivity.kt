package com.example.lovemal

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.lovemal.adapter.adapterPuppies
import com.example.lovemal.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private var PATH_PUPPIES = "puppies/"

    private var puppiesList: MutableList<Pet> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Inicializa la base de datos y referencia
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(PATH_PUPPIES)

        // Carga las mascotas
        loadPets()
    }

    private fun managePets() {
        // Obtener la referencia del ListView
        val listView = findViewById<ListView>(R.id.petList)

        // Verifica que listView no sea nulo
        if (listView != null) {
            // Usar el adaptador personalizado adapterPets
            val adapter = adapterPuppies(this, puppiesList)

            // Asignar el adaptador al ListView
            listView.adapter = adapter
        } else {
            Log.e(ContentValues.TAG, "ListView es nulo. Verifica que el id sea correcto y que el layout se haya inflado.")
        }
    }

    private fun loadPets() {
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myPuppy = singleSnapshot.getValue(Pet::class.java)
                    Log.i(ContentValues.TAG, "Encontró mascota: " + myPuppy?.nombre)
                    if (myPuppy != null) {
                        puppiesList.add(myPuppy)
                    }
                }
                // Llamar a managePets después de cargar las mascotas
                managePets()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "error en la consulta", databaseError.toException())
            }
        })
    }
}