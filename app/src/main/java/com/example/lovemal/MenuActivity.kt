package com.example.lovemal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {

    private lateinit var currentUserUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        currentUserUid = intent.getStringExtra("current_user_uid")!!

        manageButtons()
    }

    private fun manageButtons(){
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)
        btnPerfil.setOnClickListener { goToPerfil()  }
        val btnFindMatch = findViewById<Button>(R.id.btnFindMatch)
        btnFindMatch.setOnClickListener { goToPortal()  }
        val btnVetGPS = findViewById<Button>(R.id.btnVetGPS)
        btnVetGPS.setOnClickListener { goToVetGPS()  }
        val btnMarkPet = findViewById<Button>(R.id.btnMarkPet)
        btnMarkPet.setOnClickListener { goToMarkPet()  }
        val btnBuyPartner = findViewById<Button>(R.id.btnBuyPet)
        btnBuyPartner.setOnClickListener { goToFindPartner()  }
    }

    private fun goToPerfil(){
        val intent = Intent(this, Perfil::class.java)
        startActivity(intent)
    }

    private fun goToPortal(){
        val intent =Intent(this, Portal::class.java)
        startActivity(intent)
    }

    private fun goToMarkPet(){
        val intent = Intent(this, RegistrationPetActivity::class.java)
        startActivity(intent)
    }
    private fun goToVetGPS(){
        val intent = Intent(this, VetGPS_Activity::class.java)
        startActivity(intent)
    }

    private fun goToFindPartner(){
        val intent = Intent(this, MarketActivity::class.java)
        startActivity(intent)
    }

}