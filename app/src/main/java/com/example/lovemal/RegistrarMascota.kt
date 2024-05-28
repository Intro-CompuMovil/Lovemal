package com.example.lovemal

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lovemal.databinding.ActivityRegistrarMascotaBinding
import com.example.lovemal.models.Pet
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class RegistrarMascota : AppCompatActivity() {

    private lateinit var currentUserUid: String
    private lateinit var binding: ActivityRegistrarMascotaBinding

    private val PATH_PETS = "pets/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserUid = intent.getStringExtra("currentUserUid")!!

        addPet()

    }

    private fun validateForm(): Boolean {
        var valid = true

        // Validar nombre
        val name = binding.txtNombre.text.toString()
        if (TextUtils.isEmpty(name)) {
            binding.txtNombre.error = "Required."
            valid = false
        } else {
            binding.txtNombre.error = null
        }

        // Validar descripción
        val descripcion = binding.txtDescripcion.text.toString()
        if (TextUtils.isEmpty(descripcion)) {
            binding.txtDescripcion.error = "Required."
            valid = false
        } else {
            binding.txtDescripcion.error = null
        }

        // Validar raza
        val raza = binding.txtRaza.text.toString()
        if (TextUtils.isEmpty(raza)) {
            binding.txtRaza.error = "Required."
            valid = false
        } else {
            binding.txtRaza.error = null
        }

        // Validar edad
        val edad = binding.txtEdad.text.toString()
        if (TextUtils.isEmpty(edad)) {
            binding.txtEdad.error = "Required."
            valid = false
        } else {
            binding.txtEdad.error = null
        }

        // Validar altura
        val altura = binding.txtAltura.text.toString()
        if (TextUtils.isEmpty(altura)) {
            binding.txtAltura.error = "Required."
            valid = false
        } else {
            binding.txtAltura.error = null
        }

        // Validar peso
        val peso = binding.txtPeso.text.toString()
        if (TextUtils.isEmpty(peso)) {
            binding.txtPeso.error = "Required."
            valid = false
        } else {
            binding.txtPeso.error = null
        }

        // Validar selección del RadioGroup de animal
        if (binding.radioGroupMascota.checkedRadioButtonId == -1) {
            binding.radioGroupError.visibility = View.VISIBLE
            valid = false
        } else {
            binding.radioGroupError.visibility = View.GONE
        }

        // Validar selección del RadioGroup de sexo
        if (binding.radioGroupMascota2.checkedRadioButtonId == -1) {
            binding.radioGroupError2.visibility = View.VISIBLE
            valid = false
        } else {
            binding.radioGroupError2.visibility = View.GONE
        }

        return valid
    }

    private fun addPet(){
            database = FirebaseDatabase.getInstance()
            myRef = database.getReference(PATH_PETS)
            binding.btnNextStep.setOnClickListener {
                if (validateForm()) {
                    val myPet = Pet()
                    myPet.nombre = binding.txtNombre.text.toString()
                    myPet.descripcion = binding.txtDescripcion.text.toString()
                    myPet.altura = binding.txtAltura.text.toString().toInt()
                    myPet.edad = binding.txtEdad.text.toString().toInt()
                    myPet.raza = binding.txtRaza.text.toString()
                    myPet.keyUser = currentUserUid
                    myPet.peso = binding.txtPeso.text.toString().toInt()
                    myPet.key = UUID.randomUUID().toString()
                    myPet.animal = when (binding.radioGroupMascota.checkedRadioButtonId) {
                        R.id.radioPerro -> "Perro"
                        R.id.radioGato -> "Gato"
                        else -> ""
                    }
                    myPet.sexo = when (binding.radioGroupMascota2.checkedRadioButtonId){
                        R.id.radioMacho -> "Macho"
                        R.id.radioHembra -> "Hembra"
                        else -> ""
                    }
                    myRef = database.getReference(PATH_PETS + myPet.key)
                    myRef.setValue(myPet)

                    val intent = Intent(this, Perfil::class.java).apply {
                        putExtra("currentUserUid", currentUserUid)
                    }
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
                }
            }
    }
}