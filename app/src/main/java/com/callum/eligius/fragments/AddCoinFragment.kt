package com.callum.eligius.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.callum.eligius.R
import com.callum.eligius.databinding.FragmentAddCoinBinding
import com.callum.eligius.main.Main
import com.callum.eligius.models.CoinModel
import com.callum.eligius.models.PortfolioModel

class AddCoinFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentAddCoinBinding? = null
    private val fragBinding get() = _fragBinding!!
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
            edit = true
        }

        var manager = parentFragmentManager

        fragBinding.cancel.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            portfolio?.let { it1 -> CoinListFragment.newInstance(it1) }?.let { it2 ->
                transaction.replace(R.id.fragmentContainer,
                    it2
                )
            }
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.addCoin.setOnClickListener {
            var coinSelected = fragBinding.coinSelected.value
            var coinAmount = fragBinding.coinAmount.text.toString()

            if (edit) {
                for (num in 0 until portfolio?.coins?.size!!) {
                    if (coin?.name == portfolio?.coins?.get(num)?.name) {
                        portfolio?.coins?.get(num)?.name = coinSelected
                        portfolio?.coins?.get(num)?.amount = coinAmount

                        val transaction = manager.beginTransaction()
                        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        portfolio?.let { it1 -> CoinListFragment.newInstance(it1) }?.let { it2 ->
                            transaction.replace(R.id.fragmentContainer,
                                it2
                            )
                        }
                        transaction.commit()
                    }
                }

            } else {
                portfolio?.coins?.add(CoinModel(1, coinSelected, coinAmount, 200))

                val transaction = manager.beginTransaction()
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                portfolio?.let { it1 -> CoinListFragment.newInstance(it1) }?.let { it2 ->
                    transaction.replace(R.id.fragmentContainer,
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