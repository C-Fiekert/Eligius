package com.callum.eligius.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.callum.eligius.R
import com.callum.eligius.databinding.FragmentDonateBinding
import com.callum.eligius.databinding.FragmentEditProfileBinding
import com.callum.eligius.databinding.FragmentProfileBinding
import com.callum.eligius.helpers.showImagePicker
import com.callum.eligius.main.Main
import com.callum.eligius.models.UserModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.io.File

class EditProfileFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentEditProfileBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>

    private lateinit var profileImage: ImageView
    private lateinit var editName: TextInputEditText

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var reference: StorageReference
    var profile: Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Portfolios"

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app").reference
        user = auth.currentUser!!
        storage = Firebase.storage

        var manager = parentFragmentManager
        var back = fragBinding.backButton
        var save = fragBinding.save
        profileImage = fragBinding.profileImage
        var editImage = fragBinding.editImage
        editName = fragBinding.editName2

        if (user != null) {
            db.child("users").child(user.uid).child("id").get().addOnSuccessListener {

                reference = storage.getReference("userImages/" + user.uid + ".jpg")
                var localfile: File = File.createTempFile("tempfile", ".jpg")
                reference.getFile(localfile).addOnSuccessListener {
                    var bitmap: Bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    profileImage.setImageBitmap(bitmap)
                    profileImage.maxHeight = 300
                }.addOnFailureListener {
                    println("No profile picture")
                }
            }
            db.child("users").child(user.uid).child("name").get().addOnSuccessListener {
                var name = it.value.toString()
                editName.setText(name)
            }
            db.child("users").child(auth.currentUser!!.uid).child("darkmode").get().addOnSuccessListener {
                var darkmode = it.value.toString()
                val editBox = fragBinding.editName
                val editText = fragBinding.editName2
                if (darkmode == "false") {
                    editBox.boxStrokeColor = Color.BLACK
                    editBox.hintTextColor = ColorStateList.valueOf(Color.BLACK)
                    editText.setTextColor(Color.BLACK)
                } else {
                    editBox.boxStrokeColor = Color.WHITE
                    editBox.hintTextColor = ColorStateList.valueOf(Color.WHITE)
                    editText.setTextColor(Color.WHITE)
                }
            }
        }

        editImage.setOnClickListener {
            profile = Uri.EMPTY
            showImagePicker(imageIntentLauncher)
        }

        back.setOnClickListener {
            val fragment = ProfileFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        save.setOnClickListener {
            val editName = editName.text.toString()

            if (user != null) {
                user.email?.let {
                        it1 -> updateProfile(user.uid, editName, profile)
                }
            }

            val fragment = ProfileFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        registerImagePickerCallback()

        return root
    }

    private fun updateProfile(id: String, name: String, image: Uri) {
        var storageRef = storage.reference
        var profileRef: StorageReference?

        if (image != Uri.EMPTY) {
            profileRef = storageRef.child("userImages/" + id + ".jpg")

            if (profileRef != null) { profileRef.putFile(image) }
        } else {
            profileRef = storageRef.child("userImages/empty.png")
        }
        Thread.sleep(1_000)

        var pfp = profileRef.toString()
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app").reference
        db.child("users").child(id).child("name").setValue(name)
        db.child("users").child(id).child("imagesRef").setValue(pfp)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    Activity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Result ${result.data!!.data}")
                            profile = result.data!!.data!!
                            Picasso.get().load(profile).into(profileImage)
                        } // end of if
                    }
                    Activity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}