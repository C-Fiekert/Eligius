package com.callum.eligius.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.callum.eligius.R
import com.callum.eligius.adapters.PortfolioAdapter
import com.callum.eligius.databinding.FragmentDonateBinding
import com.callum.eligius.databinding.FragmentPortfolioListBinding
import com.callum.eligius.main.Main

class DonateFragment : Fragment(R.layout.fragment_donate) {

    lateinit var app: Main
    private var _fragBinding: FragmentDonateBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentDonateBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Donate to Us!"

        var switch = fragBinding.switch1


        switch.setOnClickListener {
            if (!switch.isChecked) {
                fragBinding.address.text = getString(R.string.bitcoinAddress)
                fragBinding.qr.setImageResource(R.drawable.bitcoinqr)
            } else {
                fragBinding.address.text = getString(R.string.cardanoAddress)
                fragBinding.qr.setImageResource(R.drawable.cardanoqr)
            }

        }

        return root;
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