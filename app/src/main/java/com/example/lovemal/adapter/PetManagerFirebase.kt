import android.util.Log
import com.example.lovemal.models.Pet
import com.google.firebase.database.*

class PetManagerFirebase {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private val PATH_PETS = "pets/"
    private val petsList: MutableList<Pet> = mutableListOf()

    interface PetLoadCallback {
        fun onPetsLoaded(pets: List<Pet>)
    }

    fun loadPets(callback: PetLoadCallback) {
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference(PATH_PETS)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                petsList.clear()
                for (singleSnapshot in dataSnapshot.children) {
                    val myPet = singleSnapshot.getValue(Pet::class.java)
                    Log.i("PetManagerFirebase", "Encontró mascota: " + myPet?.nombre)
                    if (myPet != null) {
                        petsList.add(myPet)
                    }
                }
                callback.onPetsLoaded(petsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("PetManagerFirebase", "error en la consulta", databaseError.toException())
            }
        })
    }
}