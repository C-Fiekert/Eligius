package com.callum.eligius.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import com.callum.eligius.R
import com.callum.eligius.adapters.PortfolioAdapter
import com.callum.eligius.databinding.FragmentDonateBinding
import com.callum.eligius.databinding.FragmentPortfolioListBinding
import com.callum.eligius.databinding.FragmentSettingsBinding
import com.callum.eligius.main.Main
import com.callum.eligius.models.UserModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.internal.Constants
import com.google.android.gms.samples.wallet.util.PaymentsUtil
import com.google.android.gms.wallet.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.File

class SettingsFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentSettingsBinding? = null
    private val fragBinding get() = _fragBinding!!

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

        _fragBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Settings"

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app").reference
        user = auth.currentUser!!
        storage = Firebase.storage

        var manager = parentFragmentManager
        var switch = fragBinding.switch2
        var profileImage = fragBinding.smallProfileImage

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
            db.child("users").child(user.uid).child("darkmode").get().addOnSuccessListener {
                var darkmode = it.value.toString()
                val scaffold = activity?.findViewById<ConstraintLayout>(R.id.scaffold)
                if (darkmode == "false") {
                    switch.isChecked = false
                    if (scaffold != null) {
                        scaffold.setBackgroundResource(R.drawable.light)
                    }
                } else {
                    switch.isChecked = true
                    if (scaffold != null) {
                        scaffold.setBackgroundResource(R.drawable.dark)
                    }
                }
            }
        }

        profileImage.setOnClickListener {
            val fragment = ProfileFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        switch.setOnClickListener {
            val scaffold = activity?.findViewById<ConstraintLayout>(R.id.scaffold)
            if (!switch.isChecked) {
                if (scaffold != null) {
                    scaffold.setBackgroundResource(R.drawable.light)
                    db.child("users").child(user.uid).child("darkmode").setValue("false").addOnCompleteListener { Timber.i("Advert added") }
                }
            } else {
                if (scaffold != null) {
                    scaffold.setBackgroundResource(R.drawable.dark)
                    db.child("users").child(user.uid).child("darkmode").setValue("true").addOnCompleteListener { Timber.i("Advert added") }
                }
            }

        }

        return root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PortfolioListFragment().apply {
                arguments = Bundle().apply {}
            }
    }

}