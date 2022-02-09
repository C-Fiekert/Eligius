package com.callum.eligius.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.callum.eligius.R
import com.callum.eligius.databinding.FragmentAboutUsBinding
import com.callum.eligius.main.Main
import com.google.android.material.bottomnavigation.BottomNavigationView

class AboutUsFragment : Fragment() {

    lateinit var app: Main
    private var _fragBinding: FragmentAboutUsBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var nav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentAboutUsBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "About Us"

        var manager = parentFragmentManager
        var donate = fragBinding.donateButton
        nav = activity?.findViewById(R.id.bottomNavigationView)!!

        donate.setOnClickListener {
            val fragment = DonateFragment()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.addToBackStack(null)
            nav.menu.getItem(2).isChecked = true
            transaction.commit()
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
            DonateFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}