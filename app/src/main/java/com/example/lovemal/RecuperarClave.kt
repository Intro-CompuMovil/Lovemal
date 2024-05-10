package com.example.lovemal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RecuperarClave : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_clave)

        sendEmail()
    }

    private fun sendEmail(){
        val btnCorreo = findViewById<Button>(R.id.btnCorreo)

        btnCorreo.setOnClickListener {
            val txtEmail = findViewById<EditText>(R.id.editTxtCorreo).text.toString()
            if(txtEmail.isEmpty())
                Toast.makeText(this, "Escriba el correo", Toast.LENGTH_SHORT).show()
            else{
                if (!validMail(txtEmail)) {
                    Toast.makeText(this, "El correo electrónico es inválido", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    Toast.makeText(this, "Correo enviado exitosamente", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }
            }
        }

    }

    private fun validMail (correo: String): Boolean {
        val emailParameters = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailParameters.matches(correo)
    }
}