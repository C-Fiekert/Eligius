package com.callum.eligius.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.callum.eligius.R
import com.callum.eligius.databinding.FragmentAddPortfolioBinding
import com.callum.eligius.main.Main
import com.callum.eligius.models.CoinModel
import com.callum.eligius.models.PortfolioModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*

class AddPortfolioFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentAddPortfolioBinding? = null
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

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentAddPortfolioBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Add Portfolio"

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app").reference
        user = auth.currentUser!!
        storage = Firebase.storage

        var manager = parentFragmentManager
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
                    println("No profile picture")
                }
            }
            db.child("users").child(auth.currentUser!!.uid).child("darkmode").get().addOnSuccessListener {
                var darkmode = it.value.toString()
                val editBox = fragBinding.portfolioName2
                val editText = fragBinding.portfolioName
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

        profileImage.setOnClickListener {
            val fragment = ProfileFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.cancel.setOnClickListener {
            val fragment = PortfolioListFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.addPortfolio.setOnClickListener {
            val newID = UUID.randomUUID().toString()
            var name = fragBinding.portfolioName.text.toString()
            var value = 0
            var coinCards : MutableList<CoinModel> = emptyList<CoinModel>().toMutableList()

            if (name.isEmpty()) {
                Toast.makeText(activity, "Please enter a portfolio name!", Toast. LENGTH_SHORT).show()
                return@setOnClickListener
            }

            app.portfoliosStore.create(PortfolioModel(newID, name, value, coinCards))

            val fragment = PortfolioListFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
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