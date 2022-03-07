package com.callum.eligius.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.net.toUri
import com.callum.eligius.databinding.ActivityLoginBinding
import com.callum.eligius.databinding.ActivitySignUpBinding
import com.callum.eligius.main.Main
import com.callum.eligius.models.UserModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import timber.log.Timber

class SignUpActivity : AppCompatActivity() {

    lateinit var app: Main
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private lateinit var signup: ImageButton
    private lateinit var name: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var repeatPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as Main
        signup = binding.signupButton
        name = binding.name
        email = binding.email
        password = binding.password
        repeatPassword = binding.repeatPassword
        val login = binding.loginText

        auth = FirebaseAuth.getInstance()
        storage = Firebase.storage

        signup.setOnClickListener {
            val name = name.text.toString()
            val email = email.text.toString()
            val password = password.text.toString()
            val repeat = repeatPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || repeat.isEmpty()) {
                Toast.makeText(this, "Please enter the missing details", Toast. LENGTH_SHORT).show()
            }

            if (repeat == password){
                signup(name, email, password)
            } else {
                Toast.makeText(this, "These passwords do not match...", Toast. LENGTH_SHORT).show()
            }
        }

        login.setOnClickListener {
            val launcherIntent = Intent(this, LoginActivity::class.java)
            startActivityForResult(launcherIntent,0)
        }

        Timber.plant(Timber.DebugTree())

        Timber.i("Eligius App started..")
    }

    private fun signup(name: String, email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                storeUser(auth.currentUser?.uid!!, name)

                val launcherIntent = Intent(this, ContainerActivity::class.java)
                startActivityForResult(launcherIntent,0)
            } else {
                Toast.makeText(this, "An error occurred", Toast. LENGTH_SHORT).show()
                Timber.i("Sign-Up failed")
            }
        }
    }

    private fun storeUser(id: String, name: String){
        var storageRef = storage.reference
        var imagesRef: StorageReference?

        imagesRef = storageRef.child("userImages/" + id + ".jpg")

        var pfp = imagesRef.toString()
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app").reference
        db.child("users").child(id).setValue(UserModel(id, name, pfp, "false"))
    }
}