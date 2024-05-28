package com.example.lovemal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.lovemal.R
import com.example.lovemal.models.Pet

class adapterPets(private val context: Context, private val pets: List<Pet>) : BaseAdapter() {

    override fun getCount(): Int {
        return pets.size
    }

    override fun getItem(position: Int): Any {
        return pets[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater
            .from(context).inflate(R.layout.perfil_mascota, parent, false)

        val petName = view.findViewById<TextView>(R.id.txtName)
        val agePet = view.findViewById<TextView>(R.id.age_pet)
        val weightPet = view.findViewById<TextView>(R.id.weight_pet)
        val heightPet = view.findViewById<TextView>(R.id.height_pet)
        val petBreed = view.findViewById<TextView>(R.id.breed_pet)
        val description = view.findViewById<TextView>(R.id.description)

        val pet = getItem(position) as Pet

        petName.text = pet.nombre
        agePet.text = pet.edad.toString()
        weightPet.text = pet.peso.toString()
        heightPet.text = pet.altura.toString()
        petBreed.text = pet.raza
        description.text = pet.descripcion

        return view
    }
}