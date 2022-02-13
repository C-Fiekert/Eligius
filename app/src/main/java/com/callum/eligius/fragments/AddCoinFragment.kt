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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as Main

        arguments?.let {
            portfolio = it.getParcelable("portfolio")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentAddCoinBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Add Portfolio"
        fragBinding.coinSelected.minValue = 0
        fragBinding.coinSelected.maxValue = 9
        fragBinding.coinSelected.displayedValues = com.callum.eligius.helpers.coinList

        var manager = parentFragmentManager

        fragBinding.cancel.setOnClickListener {
            val fragment = PortfolioListFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.addCoin.setOnClickListener {
            var coinSelected = fragBinding.coinSelected.value.toString()
            var coinAmount = fragBinding.coinAmount.text.toString()

            /*val adapter = fragBinding.recyclerView2.adapter as AddCoinAdapter
            val data = adapter.coins
            for (num in 0 until data.size) {
                println("Current loop: " + num)

                var card = fragBinding.recyclerView2.findViewHolderForAdapterPosition(num)

                var coinAmount = card?.itemView?.findViewById<EditText>(R.id.coinAmount)?.text.toString()
                var coinSelected = card?.itemView?.findViewById<NumberPicker>(R.id.coinSelected)?.value.toString()


                println("Loop: " + num + ", Name: " + coinSelected)
                println("Loop: " + num + ", Amount: " + coinAmount)


                coinCards.add(CoinModel(5, coinSelected, coinAmount, 0))
            }*/

            portfolio?.coins?.add(CoinModel(1, coinSelected, coinAmount, 200))

            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
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
        fun newInstance(portfolio: PortfolioModel) =
            AddCoinFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("portfolio", portfolio)
                }
            }
    }

}