package com.callum.eligius.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.callum.eligius.R
import com.callum.eligius.adapters.AddCoinAdapter
import com.callum.eligius.databinding.FragmentAddPortfolioBinding
import com.callum.eligius.main.Main
import com.callum.eligius.models.CoinModel
import com.callum.eligius.models.PortfolioModel
import java.util.*
import kotlin.collections.ArrayList

class AddPortfolioFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentAddPortfolioBinding? = null
    private val fragBinding get() = _fragBinding!!
    var coins = arrayListOf(1)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentAddPortfolioBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Add Portfolio"

        fragBinding.recyclerView2.layoutManager = LinearLayoutManager(activity)
        fragBinding.recyclerView2.adapter = AddCoinAdapter(coins)

        var manager = parentFragmentManager
        coins = arrayListOf(1)

        fragBinding.newCoin.setOnClickListener {
            coins.add(coins.size + 1)
            loadAdverts()
        }

        fragBinding.cancel.setOnClickListener {
            val fragment = PortfolioListFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.addPortfolio.setOnClickListener {
            val newID = UUID.randomUUID().toString()
            var name = fragBinding.portfolioName.text.toString()
            var value = 0
            var coins : MutableList<CoinModel> = emptyList<CoinModel>().toMutableList()
            for (num in 0 until fragBinding.recyclerView2.size) {
                var card = fragBinding.recyclerView2.findViewHolderForAdapterPosition(num)
                var coinAmount = card?.itemView?.findViewById<EditText>(R.id.coinAmount)?.text.toString()
                var coinSelected = card?.itemView?.findViewById<NumberPicker>(R.id.coinSelected)?.value.toString()

                coins.add(CoinModel(5, coinSelected, coinAmount, 0))
            }

            app.portfoliosStore.create(PortfolioModel(0, name, value, coins))
            println(app.portfoliosStore)

            val fragment = PortfolioListFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        loadAdverts()

        return root;
    }

    private fun loadAdverts() {
        showAdverts(coins)
    }

    fun showAdverts (coins: ArrayList<Int>) {
        fragBinding.recyclerView2.adapter = AddCoinAdapter(coins)
        fragBinding.recyclerView2.adapter?.notifyDataSetChanged()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

//    override fun onResume() {
//        super.onResume()
//        totalDonated = app.donationsStore.findAll().sumOf { it.amount }
//        fragBinding.progressBar.progress = totalDonated
//        fragBinding.totalSoFar.text = "$$totalDonated"
//    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PortfolioListFragment().apply {
                arguments = Bundle().apply {}
            }
    }

}