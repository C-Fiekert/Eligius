package com.callum.eligius.fragments

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.callum.eligius.R
import com.callum.eligius.adapters.PortfolioAdapter
import com.callum.eligius.adapters.PortfolioListener
import com.callum.eligius.databinding.FragmentPortfolioListBinding
import com.callum.eligius.main.Main
import com.callum.eligius.models.CoinModel
import com.callum.eligius.models.PortfolioModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PortfolioListFragment : Fragment(), PortfolioListener {

    lateinit var app: Main
    private var _fragBinding: FragmentPortfolioListBinding? = null
    private val fragBinding get() = _fragBinding!!

    private lateinit var db: DatabaseReference

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
        fragBinding.recyclerView1.adapter = PortfolioAdapter(app.portfoliosStore.findAll(), null, this@PortfolioListFragment)

        var manager = parentFragmentManager
        var create = fragBinding.floatingButton

        create.setOnClickListener {
            val fragment = AddPortfolioFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        fragBinding.portfolioSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("Text provided")
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                fragBinding.recyclerView1.adapter = null
                fragBinding.recyclerView1.adapter = PortfolioAdapter(app.portfoliosStore.findAll(), text, this@PortfolioListFragment)
                return true
            }
        })

        return root
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