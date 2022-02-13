package com.callum.eligius.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.callum.eligius.R
import com.callum.eligius.adapters.PortfolioCoinAdapter
import com.callum.eligius.databinding.FragmentCoinListBinding
import com.callum.eligius.main.Main
import com.callum.eligius.models.PortfolioModel

class CoinListFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentCoinListBinding? = null
    private val fragBinding get() = _fragBinding!!
    var coinList: PortfolioModel? = null

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
        fragBinding.recyclerView3.adapter = coinList?.let { PortfolioCoinAdapter(it.coins) }

        var manager = parentFragmentManager

        fragBinding.addCoin.setOnClickListener {
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            coinList?.let { it1 -> AddCoinFragment.newInstance(it1) }?.let { it2 ->
                transaction.replace(R.id.fragmentContainer,
                    it2
                )
            }
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.back.setOnClickListener {
            val fragment = PortfolioListFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragmentContainer, fragment)
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
        fun newInstance(portfolio: PortfolioModel) =
            CoinListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("coinlist", portfolio)
                }
            }
    }

}