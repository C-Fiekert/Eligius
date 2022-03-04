package com.callum.eligius.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.callum.eligius.R
import com.callum.eligius.adapters.PortfolioAdapter
import com.callum.eligius.adapters.PortfolioListener
import com.callum.eligius.databinding.FragmentPortfolioListBinding
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

class PortfolioListFragment : Fragment(), PortfolioListener {

    lateinit var app: Main
    private var _fragBinding: FragmentPortfolioListBinding? = null
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

        _fragBinding = FragmentPortfolioListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Portfolios"

        fragBinding.recyclerView1.layoutManager = LinearLayoutManager(activity)
        fragBinding.recyclerView1.adapter = PortfolioAdapter(app.portfoliosStore.findAll(), null, this@PortfolioListFragment)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app").reference
        user = auth.currentUser!!
        storage = Firebase.storage

        var manager = parentFragmentManager
        var create = fragBinding.floatingButton
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
                    if (scaffold != null) {
                        scaffold.setBackgroundResource(R.drawable.light)
                    }
                } else {
                    if (scaffold != null) {
                        scaffold.setBackgroundResource(R.drawable.dark)
                    }
                }
            }
        }

        create.setOnClickListener {
            val fragment = AddPortfolioFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        profileImage.setOnClickListener {
            val fragment = ProfileFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        fragBinding.portfolioSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("Text provided")
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                fragBinding.recyclerView1.adapter = null
                fragBinding.recyclerView1.adapter = PortfolioAdapter(app.portfoliosStore.findAll(), text, this@PortfolioListFragment)
                return true
            }
        })

        return root
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

    override fun onPortfolioClick(portfolio: PortfolioModel) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.replace(R.id.fragment, CoinListFragment.newInstance(portfolio))
        transaction.addToBackStack(null)
        transaction.commit()
    }
}