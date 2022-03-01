package com.callum.eligius.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import com.callum.eligius.R
import com.callum.eligius.adapters.PortfolioAdapter
import com.callum.eligius.databinding.FragmentDonateBinding
import com.callum.eligius.databinding.FragmentPortfolioListBinding
import com.callum.eligius.databinding.FragmentSettingsBinding
import com.callum.eligius.main.Main
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.internal.Constants
import com.google.android.gms.samples.wallet.util.PaymentsUtil
import com.google.android.gms.wallet.*
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    lateinit var app: Main
    private var _fragBinding: FragmentSettingsBinding? = null
    private val fragBinding get() = _fragBinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = "Settings"

        var switch = fragBinding.switch2


        switch.setOnClickListener {
            val scaffold = activity?.findViewById<ConstraintLayout>(R.id.scaffold)

            if (!switch.isChecked) {
                if (scaffold != null) {
                    scaffold.setBackgroundResource(R.drawable.light)
                }
            } else {
                if (scaffold != null) {
                    scaffold.setBackgroundResource(R.drawable.dark)
                }
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
        fun newInstance() =
            PortfolioListFragment().apply {
                arguments = Bundle().apply {}
            }
    }

}