package com.callum.eligius.fragments

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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber
import java.util.*

class AddCoinFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentAddCoinBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var db: DatabaseReference
    var portfolio: PortfolioModel? = null
    var coin: CoinModel? = null
    var edit = false


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
            fragBinding.coinAmount.text.append(coin!!.amount)
            fragBinding.delete.visibility = View.VISIBLE
            edit = true
        }

        var manager = parentFragmentManager

        fragBinding.cancel.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            portfolio?.let { it1 -> CoinListFragment.newInstance(it1) }?.let { it2 ->
                transaction.replace(R.id.fragmentContainer,
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

            if (coinAmount.isEmpty()) {
                Toast.makeText(activity, "Please enter a coin amount!", Toast. LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (edit) {
                for (num in 0 until portfolio?.coins?.size!!) {
                    if (coin?.name == portfolio?.coins?.get(num)?.name) {
                        portfolio?.coins?.get(num)?.name = coinSelected
                        portfolio?.coins?.get(num)?.amount = coinAmount
                        db.child("Portfolios").child(portfolio?.id.toString()).setValue(portfolio).addOnCompleteListener { Timber.i("Coin added") }

                        val transaction = manager.beginTransaction()
                        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        portfolio?.let { it1 -> CoinListFragment.newInstance(it1) }?.let { it2 ->
                            transaction.replace(R.id.fragmentContainer,
                                it2
                            )
                        }
                        transaction.commit()
                    }
                }

            } else {
                var id = UUID.randomUUID().toString()
                portfolio?.coins?.add(CoinModel(id, coinSelected, coinAmount))
                db.child("Portfolios").child(portfolio?.id.toString()).child("coins").child(id).setValue(CoinModel(id, coinSelected, coinAmount)).addOnCompleteListener { Timber.i("Coin added") }

                val transaction = manager.beginTransaction()
                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                portfolio?.let { it1 -> CoinListFragment.newInstance(it1) }?.let { it2 ->
                    transaction.replace(R.id.fragmentContainer,
                        it2
                    )
                }
                transaction.commit()
            }
        }

        fragBinding.delete.setOnClickListener {
            db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app/").reference
            portfolio?.coins?.remove(coin)

            db.child("Portfolios").child(portfolio?.id.toString()).child("coins").child(coin?.id.toString()).removeValue().addOnSuccessListener {
                Timber.i("Deleted coin successfully")
            }.addOnFailureListener {
                Timber.i("Unable to delete coin")
            }


            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            portfolio?.let { it1 -> CoinListFragment.newInstance(it1) }?.let { it2 ->
                transaction.replace(R.id.fragmentContainer,
                    it2
                )
            }
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
        fun newInstance(portfolio: PortfolioModel, coin: CoinModel?) =
            AddCoinFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("coin", coin)
                    putParcelable("portfolio", portfolio)
                }
            }
    }

}