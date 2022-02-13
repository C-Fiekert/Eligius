package com.callum.eligius.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.callum.eligius.R
import com.callum.eligius.adapters.PortfolioAdapter
import com.callum.eligius.adapters.PortfolioListener
import com.callum.eligius.databinding.FragmentPortfolioListBinding
import com.callum.eligius.main.Main
import com.callum.eligius.models.PortfolioModel

class PortfolioListFragment : Fragment(), PortfolioListener {

    lateinit var app: Main
    private var _fragBinding: FragmentPortfolioListBinding? = null
    private val fragBinding get() = _fragBinding!!

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
        fragBinding.recyclerView1.adapter = PortfolioAdapter(app.portfoliosStore.findAll(), this@PortfolioListFragment)

        var manager = parentFragmentManager
        var create = fragBinding.floatingButton

        create.setOnClickListener {
            val fragment = AddPortfolioFragment()
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

    override fun onPortfolioClick(portfolio: PortfolioModel) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.replace(R.id.fragmentContainer, CoinListFragment.newInstance(portfolio))
        transaction.addToBackStack(null)
        transaction.commit()
    }
}