package com.callum.eligius.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.callum.eligius.R
import com.callum.eligius.databinding.ActivityLoginBinding
import com.callum.eligius.main.Main
import com.callum.eligius.models.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import timber.log.Timber
import java.io.File

class LoginActivity : AppCompatActivity() {

    lateinit var app: Main
    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var login: ImageButton

    private lateinit var google: SignInButton
    private lateinit var googleSignInClient: GoogleSignInClient
    companion object{
        private const val RC_SIGN_IN = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as Main

        login = binding.loginButton
        email = binding.email2
        password = binding.password
        google = binding.googleLogin
        val signup = binding.signupText

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app").reference
        storage = Firebase.storage


        Handler().postDelayed({
            if (auth.currentUser != null){
                val launcherIntent = Intent(this, ContainerActivity::class.java)
                startActivityForResult(launcherIntent,0)
            } else {
                com.github.ajalt.timberkt.Timber.i { "User must log in" }
            }
        }, 0)


        login.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter the missing details", Toast. LENGTH_SHORT).show()
            } else {
                login(email, password)
            }
        }

        signup.setOnClickListener {
            val launcherIntent = Intent(this, SignUpActivity::class.java)
            startActivityForResult(launcherIntent,0)
        }

        /////////////////////////
        ////// Google Auth //////
        /////////////////////////

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("368862349030-utubj90vpeulssgeckel4jc2p9lvcpqn.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        google.setOnClickListener {
            signIn()
        }

        Timber.plant(Timber.DebugTree())

        Timber.i("Eligius App started..")
    }

    private fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val launcherIntent = Intent(this, ContainerActivity::class.java)
                startActivityForResult(launcherIntent,0)
            } else {
                Toast.makeText(this, "An error occurred", Toast. LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("Google Sign-In", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("Google Sign-In", "Google sign in failed", e)
                }
            } else {
                Log.w("Google Sign-In", exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Google Sign-In", "signInWithCredential:success")

                    var userID = auth.currentUser?.uid
                    var userName = auth.currentUser?.displayName
                    var pfp = auth.currentUser?.photoUrl
                    var storageRef = storage.reference
                    var profileRef: StorageReference?
                    if (userID != null) {
                        db.child("users").child(userID).get().addOnSuccessListener {
                            if (it.exists()) {
                                com.github.ajalt.timberkt.Timber.i { "User exists" }
                            } else {
                                storageRef = storage.getReference("userImages/" + userID + ".jpg")
                                if (pfp != Uri.EMPTY) {
                                    profileRef = storageRef.child("userImages/" + userID + ".jpg")
                                    if (profileRef != null) {
                                        if (pfp != null) {
                                            profileRef!!.putFile(pfp)
                                        }
                                    }
                                } else {
                                    profileRef = storageRef.child("userImages/empty.png")
                                }
                                db.child("users").child(userID).setValue(UserModel(userID, userName, profileRef.toString(), "false"))
                            }
                        }.addOnFailureListener { com.github.ajalt.timberkt.Timber.i { "Unable to perform checks on Database" } }
                    }

                    Thread.sleep(1000)
                    val launcherIntent = Intent(this, ContainerActivity::class.java)
                    startActivityForResult(launcherIntent,0)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Google Sign-In", "signInWithCredential:failure", task.exception)
                }
            }
    }
}