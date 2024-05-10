package com.example.lovemal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

class DogAdapter(private val dogs: List<Dog>) : RecyclerView.Adapter<DogAdapter.DogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mascota_adapter, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val currentDog = dogs[position]

        // Aquí estableces los datos del perro en las vistas dentro del ViewHolder
        holder.idNameDog.text = "Nombre: ${currentDog.name}"
        holder.idrRaz.text = "Raza: ${currentDog.raza}"
        holder.idedad.text = "Edad: ${currentDog.edad} años"

        // Aquí puedes configurar el ViewPager si es necesario
    }

    override fun getItemCount(): Int {
        return dogs.size
    }

    class DogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idNameDog: TextView = itemView.findViewById(R.id.idNameDog)
        val idrRaz: TextView = itemView.findViewById(R.id.idrRaz)
        val idedad: TextView = itemView.findViewById(R.id.idedad)
        val viewPager: ViewPager = itemView.findViewById(R.id.viewPager)
    }
}
