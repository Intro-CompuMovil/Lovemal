package com.example.lovemal

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MarketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_market)
        val btnApolo = findViewById<ImageButton>(R.id.apoloImageButton)
        btnApolo.setOnClickListener{goToApolo()}
        val btnOlivia = findViewById<ImageButton>(R.id.oliviaImageButton)
        btnOlivia.setOnClickListener { goToApolo() }
        val btnLupita = findViewById<ImageButton>(R.id.lupitaImageButton)
        btnLupita.setOnClickListener { goToApolo() }
        val btnLorenzo = findViewById<ImageButton>(R.id.lorenzoImageButton)
        btnLorenzo.setOnClickListener { goToApolo() }

    }
    private fun goToApolo(){
        val intent = Intent(this, ApoloActivity::class.java)
        startActivity(intent)
    }

}