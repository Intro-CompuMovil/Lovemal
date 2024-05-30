package com.example.lovemal

import android.app.ProgressDialog
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.lovemal.models.MyUser
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

class Match : AppCompatActivity() {

    private lateinit var petUid: String

    private val PATH_PETS = "pets/"
    private val PATH_USERS = "users/"
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var myRef2: DatabaseReference

    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        petUid = intent.getStringExtra("petUid")!!
        retrive_image(petUid)

        Toast.makeText(this, "¡Ponte en contacto con el dueño!", Toast.LENGTH_SHORT).show()
        showPhoneNumber()
    }

    private fun showPhoneNumber(){
        val btnWhatsApp = findViewById<Button>(R.id.btnWhatsapp)
        btnWhatsApp.setOnClickListener {
            val txtNumber = findViewById<TextView>(R.id.txtTelefono)
            database = FirebaseDatabase.getInstance()
            myRef = database.getReference(PATH_PETS)
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (singleSnapshot in dataSnapshot.children) {
                        val myPet = singleSnapshot.getValue(Pet::class.java)
                        Log.i(ContentValues.TAG, "Encontró usuario: " + myPet?.nombre)
                        if(myPet?.key == petUid){
                            myRef2 = database.getReference(PATH_USERS)
                            myRef2.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (singleSnapshot in dataSnapshot.children) {
                                        val myUser = singleSnapshot.getValue(MyUser::class.java)
                                        Log.i(ContentValues.TAG, "Encontró usuario: " + myUser?.name)
                                        if(myUser?.key == myPet.keyUser){
                                            txtNumber.text = myUser.email
                                            break
                                        }
                                    }
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.w(ContentValues.TAG, "error en la consulta", databaseError.toException())
                                }
                            })

                            break
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "error en la consulta", databaseError.toException())
                }
            })

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

        val image_view = findViewById<ImageView>(R.id.imageView4)

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