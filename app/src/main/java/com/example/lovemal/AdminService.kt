package com.example.lovemal

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.example.lovemal.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminService : Service() {

    private lateinit var database: FirebaseDatabase
    private lateinit var petRef: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance()
        petRef = database.getReference("puppies/")

        // Listener para escuchar cambios en la lista de usuarios disponibles
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Recorrer todos los usuarios
                dataSnapshot.children.forEach { petSnapshot ->
                    val pet = petSnapshot.getValue(Pet::class.java)
                    if (pet != null && pet.aprobado) {
                        // Usuario disponible, mostrar Toast
                        showToast("¡${pet.nombre} ha sido aprobada")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores si es necesario
            }
        }

        // Agregar el listener a la referencia de la base de datos
        petRef.addValueEventListener(valueEventListener)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Eliminar el listener al destruir el servicio
        petRef.removeEventListener(valueEventListener)
    }

    private fun showToast(message: String) {
        // Mostrar Toast en el hilo principal
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}
