package com.example.lovemal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lovemal.databinding.ActivityRegistrarMascotaBinding
import com.example.lovemal.models.Pet

class RegistrarMascota : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarMascotaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addPet()

    }

    private fun addPet(){
        binding.btnNextStep.setOnClickListener {
            var myPet = Pet()
            myPet.nombre = binding.txtNombre.toString()
            myPet.descripcion = binding.txtDescripcion.toString()
            
        }
    }
}