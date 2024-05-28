package com.example.lovemal

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.lovemal.models.MyUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.properties.Delegates

class MenuActivity : AppCompatActivity() {

    private lateinit var currentUserUid: String
    private val PATH_USERS = "users/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private var admin by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        currentUserUid = intent.getStringExtra("current_user_uid")!!

        isAdmin()
        manageButtons()
    }

    private fun isAdmin(){
        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        admin = false
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(PATH_USERS)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myUser = singleSnapshot.getValue(MyUser::class.java)
                    Log.i(TAG, "Encontró usuario: " + myUser?.name)
                    if(myUser?.key == currentUserUid){
                        admin = myUser.admin
                        if(!admin){
                            btnAdmin.visibility = View.INVISIBLE
                        }
                        Log.i(TAG, "El usuario es admin? " + admin)
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "error en la consulta", databaseError.toException())
            }
        })
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
        val intent = Intent(this, Perfil::class.java).apply {
            putExtra("currentUserUid", currentUserUid)
        }
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