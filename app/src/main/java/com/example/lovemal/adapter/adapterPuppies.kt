package com.example.lovemal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.lovemal.AdminActivity
import com.example.lovemal.R
import com.example.lovemal.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class adapterPuppies(private val context: Context, private val pets: List<Pet>) : BaseAdapter() {

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
            .from(context).inflate(R.layout.perfil_puppies, parent, false)

        val typePet = view.findViewById<TextView>(R.id.txttype)
        val agePet = view.findViewById<TextView>(R.id.age_pet)
        val weightPet = view.findViewById<TextView>(R.id.weight_pet)
        val heightPet = view.findViewById<TextView>(R.id.height_pet)
        val petBreed = view.findViewById<TextView>(R.id.breed_pet)
        val imgApButton = view.findViewById<ImageButton>(R.id.approve)
        val imgRjdButton = view.findViewById<ImageButton>(R.id.rejected)

        val pet = getItem(position) as Pet

        typePet.text = "Cachorro ${pet.animal}"
        agePet.text = "Edad: ${pet.edad}"
        weightPet.text = "Peso: ${pet.peso}kg"
        heightPet.text = "Altura: ${pet.altura}cm"
        petBreed.text = "Raza: ${pet.raza}"

        imgApButton.setOnClickListener {
            database = FirebaseDatabase.getInstance()
            myRef = database.getReference(PATH_PUPPIES)

            val petRef = myRef.child(pet.key)

            petRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // El objeto existe, actualizar el valor
                        val pet = dataSnapshot.getValue(Pet::class.java)
                        pet?.let {
                            it.aprobado = true // Modificar el valor deseado
                            petRef.setValue(it) // Guardar los cambios en la base de datos
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

        imgRjdButton.setOnClickListener {
            // Aquí puedes realizar la acción que desees cuando se haga clic en el ImageButton
            // Por ejemplo, abrir una nueva actividad, mostrar un diálogo, etc.
        }

        return view
    }
}