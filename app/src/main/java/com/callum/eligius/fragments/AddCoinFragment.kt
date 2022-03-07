package com.callum.eligius.fragments

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.callum.eligius.R
import com.callum.eligius.databinding.FragmentAddCoinBinding
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
import timber.log.Timber
import java.io.File
import java.util.*

class AddCoinFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentAddCoinBinding? = null
    private val fragBinding get() = _fragBinding!!
    var portfolio: PortfolioModel? = null
    var coin: CoinModel? = null
    var edit = false

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var db: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var reference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main

        arguments?.let {
            portfolio = it.getParcelable("portfolio")
            coin = it.getParcelable("coin")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentAddCoinBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        fragBinding.coinSelected.minValue = 0
        fragBinding.coinSelected.maxValue = 9
        fragBinding.coinSelected.displayedValues = com.callum.eligius.helpers.coinList
        if (coin != null) {
            fragBinding.coinSelected.value = coin!!.name
            fragBinding.coinAmount.text?.append(coin!!.amount)
            fragBinding.isFavourited.isChecked = coin!!.favourited
            fragBinding.addCoin.setImageResource(R.drawable.save)
            edit = true
        }

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
                val editBox = fragBinding.coinAmount2
                val editText = fragBinding.coinAmount
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
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            portfolio?.let { it1 -> CoinListFragment.newInstance(it1) }?.let { it2 ->
                transaction.replace(R.id.fragment,
                    it2
                )
            }
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.addCoin.setOnClickListener {
            db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app/").reference
            var coinSelected = fragBinding.coinSelected.value
            var coinAmount = fragBinding.coinAmount.text.toString()
            var favourited = fragBinding.isFavourited.isChecked

            if (coinAmount.isEmpty()) {
                Toast.makeText(activity, "Please enter a coin amount!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (edit) {
                for (num in 0 until portfolio?.coins?.size!!) {
                    if (coin?.name == portfolio?.coins?.get(num)?.name) {
                        portfolio?.coins?.get(num)?.name = coinSelected
                        portfolio?.coins?.get(num)?.amount = coinAmount
                        portfolio?.coins?.get(num)?.favourited = favourited

                        portfolio?.coins?.get(num)?.id?.let { it1 ->
                            db.child("users").child(user.uid).child("portfolios").child(portfolio?.id.toString()).child("coins").child(it1).child("name").setValue(coinSelected).addOnCompleteListener {
                                Timber.i("Coin added")
                            }
                            db.child("users").child(user.uid).child("portfolios").child(portfolio?.id.toString()).child("coins").child(it1).child("amount").setValue(coinAmount).addOnCompleteListener {
                                Timber.i("Coin added")
                            }
                            db.child("users").child(user.uid).child("portfolios").child(portfolio?.id.toString()).child("coins").child(it1).child("favourited").setValue(favourited).addOnCompleteListener {
                                Timber.i("Coin added")
                            }
                        }

                        portfolio?.coins?.get(num)?.id?.let { it1 ->
                            db.child("portfolios").child(portfolio?.id.toString()).child("coins").child(it1).child("name").setValue(coinSelected).addOnCompleteListener {
                                Timber.i("Coin added")
                            }
                            db.child("portfolios").child(portfolio?.id.toString()).child("coins").child(it1).child("amount").setValue(coinAmount).addOnCompleteListener {
                                Timber.i("Coin added")
                            }
                            db.child("portfolios").child(portfolio?.id.toString()).child("coins").child(it1).child("favourited").setValue(favourited).addOnCompleteListener {
                                Timber.i("Coin added")
                            }
                        }

                        val transaction = manager.beginTransaction()
                        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        portfolio?.let { it1 -> CoinListFragment.newInstance(it1) }?.let { it2 ->
                            transaction.replace(R.id.fragment,
                                it2
                            )
                        }
                        transaction.commit()
                    }
                }

            } else {
                var id = UUID.randomUUID().toString()
                portfolio?.coins?.add(CoinModel(id, coinSelected, coinAmount, 0, favourited))
                db.child("users").child(user.uid).child("portfolios").child(portfolio?.id.toString()).child("coins").child(id).setValue(CoinModel(id, coinSelected, coinAmount, 0, favourited)).addOnCompleteListener { Timber.i("Coin added") }
                db.child("portfolios").child(portfolio!!.id).child("coins").child(id).setValue(CoinModel(id, coinSelected, coinAmount, 0, favourited)).addOnCompleteListener { Timber.i("Coin added") }

                val transaction = manager.beginTransaction()
                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                portfolio?.let { it1 -> CoinListFragment.newInstance(it1) }?.let { it2 ->
                    transaction.replace(R.id.fragment,
                        it2
                    )
                }
                transaction.commit()
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
        fun newInstance(portfolio: PortfolioModel, coin: CoinModel?) =
            AddCoinFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("coin", coin)
                    putParcelable("portfolio", portfolio)
                }
            }
    }

}