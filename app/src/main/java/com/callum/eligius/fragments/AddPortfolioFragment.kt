package com.callum.eligius.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.callum.eligius.R
import com.callum.eligius.databinding.FragmentAddPortfolioBinding
import com.callum.eligius.main.Main
import com.callum.eligius.models.CoinModel
import com.callum.eligius.models.PortfolioModel

class AddPortfolioFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentAddPortfolioBinding? = null
    private val fragBinding get() = _fragBinding!!
    var coins = arrayListOf(1)


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


        var manager = parentFragmentManager


        fragBinding.cancel.setOnClickListener {
            val fragment = PortfolioListFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fragBinding.addPortfolio.setOnClickListener {
            var name = fragBinding.portfolioName.text.toString()
            var value = 0
            var coinCards : MutableList<CoinModel> = emptyList<CoinModel>().toMutableList()

            if (name.isEmpty()) {
                Toast.makeText(activity, "Please enter a portfolio name!", Toast. LENGTH_SHORT).show()
                return@setOnClickListener
            }

            app.portfoliosStore.create(PortfolioModel(0, name, value, coinCards))

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
        fun newInstance() =
            PortfolioListFragment().apply {
                arguments = Bundle().apply {}
            }
    }

}