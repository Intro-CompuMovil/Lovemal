package com.example.lovemal

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lovemal.databinding.ActivityRegistrarseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Registrarse : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private val CAMERA_REQUEST_CODE = 101

    private lateinit var binding: ActivityRegistrarseBinding
    private lateinit var auth: FirebaseAuth
    private val PATH_USERS = "users/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askPermissionCamera()
        agregarUsuario()
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = binding.txtNombre.text.toString()
        if (TextUtils.isEmpty(name)) {
            binding.txtNombre.error = "Required."
            valid = false
        } else {
            binding.txtNombre.error = null
        }

        val email = binding.txtCorreo.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.txtCorreo.error = "Required."
            valid = false
        } else {
            binding.txtCorreo.error = null
        }

        val password = binding.txtClave.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.txtClave.error = "Required."
            valid = false
        } else {
            binding.txtClave.error = null
        }

        val confirmPass = binding.txtConfirmarClave.text.toString()
        if (TextUtils.isEmpty(confirmPass)) {
            binding.txtConfirmarClave.error = "Required."
            valid = false
        } else {
            binding.txtConfirmarClave.error = null
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

    private fun validatePassword(pass: String, confirmPass: String): Boolean{
        if(pass == confirmPass)
            return true;
        return false
    }

    private fun agregarUsuario() {
        val btnContinue = findViewById<Button>(R.id.btnNextStep)
        btnContinue.setOnClickListener{
            val database = Firebase.database
            val textEmail = findViewById<EditText>(R.id.txtCorreo).text.toString()
            val textName = findViewById<EditText>(R.id.txtNombre).text.toString()
            val textPassword = findViewById<EditText>(R.id.txtClave).text.toString()
            val textConfirm = findViewById<EditText>(R.id.txtConfirmarClave).text.toString()

            if(isEmailValid(textEmail) && validateForm() && validatePassword(textPassword, textConfirm)){
                auth = Firebase.auth
                auth.createUserWithEmailAndPassword(binding.txtCorreo.text.toString(), binding.txtClave.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(ContentValues.TAG, "createUserWithEmail:onComplete:" + task.isSuccessful)
                            val myUser = MyUser()
                            myUser.name = binding.txtNombre.text.toString()
                            myUser.email = binding.txtCorreo.text.toString()
                            val usuarioRef = database.getReference(PATH_USERS + auth.currentUser!!.uid)
                            myUser.key = auth.currentUser!!.uid
                            usuarioRef.setValue(myUser)
                            val currentUserUid = auth.currentUser?.uid
                            //saveImageToFirebase(imagenLista!!)
                            // Inicia la siguiente actividad y envía la UID del usuario actual como extra
                            val intent = Intent(this, MenuActivity::class.java)
                            intent.putExtra("current_user_uid", currentUserUid)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this, "createUserWithEmail:Failure: " + task.exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            task.exception?.message?.let { Log.e(ContentValues.TAG, it) }
                        }
                    }
            }
            else{
                binding.txtCorreo.setText("")
                binding.txtClave.setText("")
            }
        }
    }

    private fun askPermissionCamera(){
        val btnCamara = findViewById<ImageButton>(R.id.btnCamara)
        btnCamara.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }   else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageView1 = findViewById<ImageButton>(R.id.btnCamara)
            imageView1.setImageBitmap(imageBitmap)
        }
    }

    private fun validMail (correo: String): Boolean {
        val emailParameters = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailParameters.matches(correo)
    }
}