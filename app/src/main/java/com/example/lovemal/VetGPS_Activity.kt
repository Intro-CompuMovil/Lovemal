package com.example.lovemal

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class VetGPS_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vet_gps)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        manageList()
    }

    private fun manageList(){
        val lista = findViewById<ListView>(R.id.listaVets)
        val veterinariasArray = resources.getStringArray(R.array.veterinarias)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, veterinariasArray)
        lista.adapter = adapter

        setupItemClickListener(lista)
    }

    private fun setupItemClickListener(lista: ListView) {
        lista.setOnItemClickListener { parent, view, position, id ->
            val selectedName = lista.getItemAtPosition(position).toString()

            // Crear un Intent y agregar el nombre como un extra
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("veterinaria", selectedName)

            // Iniciar la nueva actividad
            startActivity(intent)
        }
    }
}