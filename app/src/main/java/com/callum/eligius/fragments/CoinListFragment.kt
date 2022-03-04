package com.callum.eligius.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.callum.eligius.R
import com.callum.eligius.adapters.CoinListener
import com.callum.eligius.adapters.PortfolioAdapter
import com.callum.eligius.adapters.PortfolioCoinAdapter
import com.callum.eligius.databinding.FragmentCoinListBinding
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

class CoinListFragment : Fragment(), CoinListener {

    lateinit var app: Main
    private var _fragBinding: FragmentCoinListBinding? = null
    private val fragBinding get() = _fragBinding!!
    var coinList: PortfolioModel? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var reference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main

        arguments?.let {
            coinList = it.getParcelable("coinlist")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentCoinListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Coins"
        var portfolioName = coinList?.name
        fragBinding.textView3.text = portfolioName + "'s Portfolio"

        fragBinding.recyclerView3.layoutManager = LinearLayoutManager(activity)
        fragBinding.recyclerView3.adapter = coinList?.let { PortfolioCoinAdapter(it.coins, null, this@CoinListFragment) }

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
                    Toast.makeText(requireContext(), "Could not load image", Toast. LENGTH_SHORT).show()
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

        fragBinding.addCoin.setOnClickListener {
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            coinList?.let { it1 -> AddCoinFragment.newInstance(it1, null) }?.let { it2 ->
                transaction.replace(R.id.fragment,
                    it2
                )
            }
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.back.setOnClickListener {
            val fragment = PortfolioListFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
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
                fragBinding.recyclerView3.adapter = null
                fragBinding.recyclerView3.adapter =
                    coinList?.let { PortfolioCoinAdapter(it.coins, text, this@CoinListFragment) }
                return true
            }
        })


        return root;
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }


    companion object {
        @JvmStatic
        fun newInstance(portfolio: PortfolioModel) =
            CoinListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("coinlist", portfolio)
                }
            }
    }

    override fun onCoinClick(coin: CoinModel) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        coinList?.let { AddCoinFragment.newInstance(it, coin) }?.let {
            transaction.replace(R.id.fragment,
                it
            )
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

}