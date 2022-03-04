package com.callum.eligius.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.callum.eligius.R
import com.callum.eligius.activities.LoginActivity
import com.callum.eligius.databinding.FragmentDonateBinding
import com.callum.eligius.databinding.FragmentPortfolioListBinding
import com.callum.eligius.databinding.FragmentProfileBinding
import com.callum.eligius.main.Main
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class ProfileFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentProfileBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var reference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentProfileBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Portfolios"

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app").reference
        user = auth.currentUser!!
        storage = Firebase.storage

        var manager = parentFragmentManager
        var profileImage = fragBinding.profileImage
        var username = fragBinding.username
        var numPortfolios = fragBinding.numPortfolios
        var numFavourites = fragBinding.numFavourites
        var editButton = fragBinding.editButton
        var signout = fragBinding.signout


        if (user != null) {
            db.child("users").child(user.uid).child("id").get().addOnSuccessListener {

                reference = storage.getReference("userImages/" + user.uid + ".jpg")
                var localfile: File = File.createTempFile("tempfile", ".jpg")
                reference.getFile(localfile).addOnSuccessListener {
                    var bitmap: Bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    profileImage.setImageBitmap(bitmap)
                    profileImage.maxHeight = 300
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Could not load image", Toast. LENGTH_SHORT).show()
                }
            }
            db.child("users").child(user.uid).child("name").get().addOnSuccessListener {
                var name = it.value.toString()
                username.text = name
            }
//            db.child("users").child(user.uid).child("portfolios").get().addOnSuccessListener {
//                var pnumber = it.value.toString()
//                numPortfolios.text = pnumber
//            }
        }

        editButton.setOnClickListener {
            val fragment = EditProfileFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        signout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        // Inflate the layout for this fragment
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}