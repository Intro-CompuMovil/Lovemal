package com.example.lovemal

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToPortal()
        registerUsser()
        forgottenPassword()
    }

    private fun forgottenPassword(){
        val btnRecuperarClave = findViewById<Button>(R.id.btnRecuperarClave)
        btnRecuperarClave.setOnClickListener {
            val intent = Intent(this, RecuperarClave::class.java)
            startActivity(intent)
        }
    }

    private fun validateForm(): Boolean {
        val email = findViewById<EditText>(R.id.EmAdrssUser)
        val password = findViewById<EditText>(R.id.password)
        var valid = true
        val txtEmail = email.text.toString()
        if (TextUtils.isEmpty(txtEmail)) {
            email.error = "Required."
            valid = false
        } else {
            email.error = null
        }
        val txtPassword = password.text.toString()
        if (TextUtils.isEmpty(txtPassword)) {
            password.error = "Required."
            valid = false
        } else {
            password.error = null
        }
        return valid
    }

    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@") ||
            !email.contains(".") ||
            email.length < 5) {
            Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun goToPortal() {
        val email = findViewById<EditText>(R.id.EmAdrssUser)
        val password = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.btnSesion)
        btnLogin.setOnClickListener {
            if(validateForm() && isEmailValid(email.text.toString())){
                auth = Firebase.auth

                auth.signInWithEmailAndPassword(
                    email.text.toString(),
                    password.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        // Sign in task
                        Log.d(ContentValues.TAG, "signInWithEmail:onComplete:" + task.isSuccessful)
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful) {
                            Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                this, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            email.setText("")
                            password.setText("")
                        }
                        else{
                            Toast.makeText(this, "Authentication done.",Toast.LENGTH_SHORT).show()
                            val currentUserUid = auth.currentUser?.uid

                            // Inicia la siguiente actividad y envía la UID del usuario actual como extra
                            val intent = Intent(this, MenuActivity::class.java)
                            intent.putExtra("current_user_uid", currentUserUid)
                            startActivity(intent)
                            finish()
                        }
                    }
            }
            else{
                email.setText("")
                password.setText("")
            }
        }
    }

    private fun registerUsser(){
        val btnRegistrarse = findViewById<Button>(R.id.btnRegister)
        btnRegistrarse.setOnClickListener{startActivity(Intent(this, Registrarse::class.java))}
    }

    private fun loadUsuariosFromJSON(): JSONArray {
        var json: String? = null
        try {
            val inputStream = assets.open("usuarios.JSON")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        val jsonObject = JSONObject(json)
        return jsonObject.getJSONArray("usuarios")
    }
}