package com.example.lovemal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.lovemal.R
import com.example.lovemal.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class sellAdapter(private val context: Context, private val pets: List<Pet>) : BaseAdapter() {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private val PATH_PUPPIES = "puppies/"

    override fun getCount(): Int {
        return pets.size
    }

    override fun getItem(position: Int): Any {
        return pets[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = convertView ?: LayoutInflater
            .from(context).inflate(R.layout.puppies_sell, parent, false)

        val typePet = view.findViewById<TextView>(R.id.txttype)
        val agePet = view.findViewById<TextView>(R.id.age_pet)
        val weightPet = view.findViewById<TextView>(R.id.weight_pet)
        val heightPet = view.findViewById<TextView>(R.id.height_pet)
        val petBreed = view.findViewById<TextView>(R.id.breed_pet)
        val adoptBtn = view.findViewById<Button>(R.id.btnAdopt)

        val pet = getItem(position) as Pet

        typePet.text = "Cachorro ${pet.animal}"
        agePet.text = "Edad: ${pet.edad}"
        weightPet.text = "Peso: ${pet.peso}kg"
        heightPet.text = "Altura: ${pet.altura}cm"
        petBreed.text = "Raza: ${pet.raza}"

        adoptBtn.setOnClickListener {

        }

        return view
    }
}