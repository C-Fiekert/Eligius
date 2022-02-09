package com.callum.eligius.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.callum.eligius.R
import com.callum.eligius.adapters.AddCoinAdapter
import com.callum.eligius.databinding.FragmentAddPortfolioBinding
import com.callum.eligius.main.Main

class AddPortfolioFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentAddPortfolioBinding? = null
    private val fragBinding get() = _fragBinding!!
    var coins = arrayListOf(1)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

//        fragBinding.cancel.setOnClickListener {
//            val fragment = PortfolioListFragment()
//            val transaction = manager.beginTransaction()
//            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//            transaction.replace(R.id.fragmentContainer, fragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }

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