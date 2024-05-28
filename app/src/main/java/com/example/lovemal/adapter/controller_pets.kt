import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.example.lovemal.models.MyUser
import com.example.lovemal.models.Pet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PetManagerFirebase() {

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    private var PATH_PETS = "pets/"

    private var petsList: MutableList<Pet> = mutableListOf()

    fun loadPets(): MutableList<Pet> {

        myRef = database.getReference(PATH_PETS)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    val myPet = singleSnapshot.getValue(Pet::class.java)
                    Log.i(TAG, "Encontró mascota: " + myPet?.nombre)
                    if (myPet != null) {
                        petsList.add(myPet)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "error en la consulta", databaseError.toException())
            }
        })

        return petsList
    }

   /* init {
        this.databaseReference = databaseReference.child(PATH_USERS) // Apply path for users
    }

    // Methods for interacting with Firebase Realtime Database
    fun addPet(userId: String?, pet: Pet?) {
        // Push the pet data to a new child node under the user's pets reference
        databaseReference.child(userId!!).child(PATH_PETS).push().setValue(pet)
    }

    fun getPets(userId: String?, listener: ValueEventListener<DataSnapshot?>?) {
        // Read all pets from the specific user's pets reference and attach a listener
        databaseReference.child(userId!!).child(PATH_PETS).addValueEventListener(listener)
    }

    fun updatePet(userId: String?, petKey: String?, updatedPet: Pet?) {
        // Update specific pet data based on its key within the user's pets reference
        databaseReference.child(userId!!).child(PATH_PETS).child(petKey!!).setValue(updatedPet)
    }

    fun deletePet(userId: String?, petKey: String?) {
        // Remove the pet data from the specific user's pets reference
        databaseReference.child(userId!!).child(PATH_PETS).child(petKey!!).removeValue()
    }

    // You can add other methods as needed for specific functionalities
    fun getUserWithPets(userId: String?, listener: ValueEventListener<DataSnapshot?>): MyUser? {
        // This method retrieves a complete MyUser object with its list of pets
        val userRef = databaseReference.child(userId!!)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(MyUser::class.java)
                if (user != null) {
                    // Extract pet data from the user object (if applicable)
                    val pets: MutableList<Pet?> = ArrayList()
                    for (petSnapshot in snapshot.child(PATH_PETS).getChildren()) {
                        val pet = petSnapshot.getValue(Pet::class.java)
                        pets.add(pet)
                    }
                    user.setPets(pets) // Assuming a setter method for pets in MyUser
                }
                listener.onDataChange(snapshot) // Pass the complete user object to the listener
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onCancelled(error)
            }
        })
        return null
    } // You can add similar methods to manage pets within MyUser as needed

    companion object {
        private const val PATH_USERS = "users/"
        private const val PATH_PETS = "pets/"
    }*/
}