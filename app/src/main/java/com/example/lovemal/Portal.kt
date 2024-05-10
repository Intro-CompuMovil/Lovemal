package com.example.lovemal

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager

class Portal : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var btnDislike: ImageButton
    private lateinit var btnLike: ImageButton
    private lateinit var perros: MutableList<Perro>
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portal)

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