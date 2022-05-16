package com.example.firebaseexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())

        auth = Firebase.auth
        if (auth.currentUser != null) {
            Timber.e("Welcome app")
           // FirebaseAuth.getInstance().signOut()
            readUserData(auth.currentUser!!.uid)
        } else {
            login("judlg@gmail.com", "123456")
        }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Timber.e("Success login")
                    val user = auth.currentUser
                    readUserData(user!!.uid)
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.e("Error login")
                }
            }
    }

    fun registration(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Timber.e("Success registration ${user?.uid}")
                    createNewUser()
                } else {
                    Timber.e("Error registration")
                }
            }
    }

    private fun createNewUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val hashMap = hashMapOf<String, Any>(
            "name" to "John doe",
            "city" to "Nairobi",
            "age" to 24
        )
        FirebaseUtils().fireStoreDatabase.collection("users").document(firebaseUser!!.uid)
            .set(hashMap)
            .addOnSuccessListener {
                Log.e("TAG", "Added document with ID ")
                Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Error adding document $exception")
            }
    }

    private fun readAllData() {

        FirebaseUtils().fireStoreDatabase.collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { document ->
                    Log.e("TAG", "Read document with ID ${document.id}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Error getting documents $exception")
            }

    }

    private fun readUserData(userId: String) {
        FirebaseUtils().fireStoreDatabase.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Timber.e("success")
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Error getting documents $exception")
            }
    }
}