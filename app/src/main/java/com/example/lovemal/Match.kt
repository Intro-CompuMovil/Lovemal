package com.example.lovemal

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lovemal.models.MyUser
import com.example.lovemal.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Match : AppCompatActivity() {

    private lateinit var petUid: String

    private val PATH_PETS = "pets/"
    private val PATH_USERS = "users/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var myRef2: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        petUid = intent.getStringExtra("petUid")!!


        Toast.makeText(this, "¡Ponte en contacto con el dueño!", Toast.LENGTH_SHORT).show()
        showPhoneNumber()
    }

    private fun showPhoneNumber(){
        val btnWhatsApp = findViewById<ImageButton>(R.id.btnWhatsapp)
        btnWhatsApp.setOnClickListener {
            val txtNumber = findViewById<TextView>(R.id.txtTelefono)
            database = FirebaseDatabase.getInstance()
            myRef = database.getReference(PATH_PETS)
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (singleSnapshot in dataSnapshot.children) {
                        val myPet = singleSnapshot.getValue(Pet::class.java)
                        Log.i(ContentValues.TAG, "Encontró usuario: " + myPet?.nombre)
                        if(myPet?.key == petUid){
                            myRef2 = database.getReference(PATH_USERS)
                            myRef2.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (singleSnapshot in dataSnapshot.children) {
                                        val myUser = singleSnapshot.getValue(MyUser::class.java)
                                        Log.i(ContentValues.TAG, "Encontró usuario: " + myUser?.name)
                                        if(myUser?.key == myPet.keyUser){
                                            txtNumber.text = myUser.email
                                            break
                                        }
                                    }
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.w(ContentValues.TAG, "error en la consulta", databaseError.toException())
                                }
                            })

                            break
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "error en la consulta", databaseError.toException())
                }
            })

        }
    }
}