package com.example.lovemal

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Match : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        Toast.makeText(this, "¡Ponte en contacto con el dueño!", Toast.LENGTH_SHORT).show()
        showPhoneNumber()
    }

    private fun showPhoneNumber(){
        val btnWhatsApp = findViewById<ImageButton>(R.id.btnWhatsapp)
        btnWhatsApp.setOnClickListener {
            val txtNumber = findViewById<TextView>(R.id.txtTelefono)
            txtNumber.text = "+57 320 456 7892"
        }
    }
}