package com.example.lovemal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

public class RecuperarClave : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_clave)
        val txtmail : TextView = findViewById(R.id.editTxtCorreo)
        val btnCambiar : Button = findViewById(R.id.btnCorreo)
        btnCambiar.setOnClickListener(){
            changePassword(txtmail.text.toString())
        }
    }

    private fun changePassword(email : String){
        firebaseAuth = Firebase.auth
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if(task.isSuccessful)
                {
                    Toast.makeText(baseContext, "Correo de cambio de contraseña enviado exitosamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(baseContext, "Error, no se pudo enviar el email para cambiar la contraseña", Toast.LENGTH_SHORT).show()
                }
            }
    }
}