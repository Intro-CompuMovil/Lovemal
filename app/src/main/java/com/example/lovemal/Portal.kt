package com.example.lovemal

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.lovemal.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class Portal : AppCompatActivity() {

    private lateinit var btnDislike: ImageButton
    private lateinit var btnLike: ImageButton
    private lateinit var petsList: MutableList<Pet>
    private lateinit var currentUserUid: String
    private val PATH_PETS = "pets/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private var raza: String = ""
    private var animal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portal)

        currentUserUid = intent.getStringExtra("currentUserUid")!!

        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(PATH_PETS)

        loadPets()

        manageButtons()
    }

    private fun loadPets(){
        petsList = mutableListOf()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myPuppy = singleSnapshot.getValue(Pet::class.java)
                    Log.i(ContentValues.TAG, "Encontró mascota: " + myPuppy?.nombre)
                    if (myPuppy != null) {
                        petsList.add(myPuppy)
                    }
                }
                updateDisplayedInfo()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "error en la consulta", databaseError.toException())
            }
        })
    }

    private fun manageButtons(){
        btnLike = findViewById(R.id.btnLike)
        btnDislike = findViewById(R.id.btnDislike)

        btnLike.setOnClickListener {
            val intent = Intent(this, Match::class.java).apply {
                putExtra("petUid", petsList[0].key)
            }
            startActivity(intent)
        }

        btnDislike.setOnClickListener {
            // Mover el primer perro al final de la lista
            val firstPet = petsList.removeAt(0)
            petsList.add(firstPet)
            // Actualizar la información y la imagen mostrada
            updateDisplayedInfo()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDisplayedInfo() {
        val txtMascota = findViewById<TextView>(R.id.txtMascota)
        for(pet: Pet in petsList){
            if(pet.keyUser == currentUserUid){
                if(pet.aprobado){
                    raza = pet.raza
                    animal = pet.animal
                    txtMascota.text = "Estas buscando pareja para: " + pet.nombre
                }
            }
        }
        if (petsList.isNotEmpty()) {
            val NombrePet = findViewById<TextView>(R.id.txtNombrePerro)
            val edadPet = findViewById<TextView>(R.id.txtEdadpet)
            val breedPet = findViewById<TextView>(R.id.txtbreed)
            val InfPet = findViewById<TextView>(R.id.txtInfPet)



            var currentPet = petsList[0]

            retrive_image(currentPet.key)

            if(currentPet.raza == raza && currentPet.animal == animal && currentPet.keyUser != currentUserUid) {
                NombrePet.text = currentPet.nombre
                edadPet.text = "Edad: ${currentPet.edad} años"
                breedPet.text = "Raza: ${currentPet.raza}"
                InfPet.text = currentPet.descripcion
            }
            else{
                val firstPet = petsList.removeAt(0)
                petsList.add(firstPet)
            }
        }
    }

    private fun downloadFiles(uid: String) {
        storage = Firebase.storage("gs://icm24101-419314.appspot.com")

        val localFile = File.createTempFile("profile_image", "jpeg")
        val imageRef = storage.reference.child("profileImages/${uid}")

        val image_view = findViewById<ImageView>(R.id.petImage)

        imageRef.getFile(localFile)
            .addOnSuccessListener { taskSnapshot ->
                // Successfully downloaded data to local file
                Glide.with(this)
                    .load(localFile.absolutePath) // Utilizar la URL de la foto
                    .placeholder(R.drawable.dog_airedale_terrier_svgrepo_com) // Placeholder mientras se carga la imagen
                    .error(R.drawable.baseline_account_box_24) // Imagen de error si la carga falla
                    .into(image_view)
                // Notificar al adaptador que los datos han cambiado
                Log.i("FBApp", "succesfully downloaded")
                // Update UI using the localFile
            }.addOnFailureListener { exception ->
                // Handle failed download
                // ...
                Toast.makeText(this,"Image Retrived Failed: "+exception.message,Toast.LENGTH_LONG).show()
            }



    }

    fun retrive_image(uid: String) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val localFile = File.createTempFile("image", "jpeg")
        val image_refrance: StorageReference = storageReference.child("profileImages/${uid}")

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Retriving Image...")
        progressDialog.setMessage("Processing...")
        progressDialog.show()

        val image_view = findViewById<ImageView>(R.id.petImage)

        image_refrance.downloadUrl.addOnSuccessListener { uri: Uri ->

            Glide.with(this)
                .load(uri)
                .into(image_view)

            progressDialog.dismiss()
            Toast.makeText(this,"Image Retrived Successfull",Toast.LENGTH_LONG).show()
        }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(this,"Image Retrived Failed: "+exception.message,Toast.LENGTH_LONG).show()

            }
    }
}