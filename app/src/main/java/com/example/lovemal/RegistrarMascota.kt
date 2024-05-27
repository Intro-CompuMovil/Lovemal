package com.example.lovemal

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lovemal.databinding.ActivityRegistrarMascotaBinding

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