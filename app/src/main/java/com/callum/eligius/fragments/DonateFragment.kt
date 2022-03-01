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
import com.callum.eligius.main.Main
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.internal.Constants
import com.google.android.gms.samples.wallet.util.PaymentsUtil
import com.google.android.gms.wallet.*
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DonateFragment : Fragment(R.layout.fragment_donate) {

    lateinit var app: Main
    private lateinit var paymentsClient: PaymentsClient
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991
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

        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()

        paymentsClient = Wallet.getPaymentsClient(requireContext(), walletOptions)

        var switch = fragBinding.switch1

        fragBinding.googlePay.setOnClickListener {
            var price = fragBinding.donateAmount.text.toString()
            if (price.isEmpty()) {
                Toast.makeText(activity, "Please enter a coin amount!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            requestPayment(price)
        }


        switch.setOnClickListener {
            if (!switch.isChecked) {
                fragBinding.address.visibility = View.VISIBLE
                fragBinding.qr.visibility = View.VISIBLE
                fragBinding.googlePay.visibility = View.INVISIBLE
                fragBinding.donateEuro.visibility = View.INVISIBLE
                fragBinding.donateAmount.visibility = View.INVISIBLE
            } else {
                fragBinding.address.visibility = View.INVISIBLE
                fragBinding.qr.visibility = View.INVISIBLE
                fragBinding.googlePay.visibility = View.VISIBLE
                fragBinding.donateEuro.visibility = View.VISIBLE
                fragBinding.donateAmount.visibility = View.VISIBLE
            }

        }

        return root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    private fun requestPayment(price: String) {
        // Disables the button to prevent multiple clicks.
        fragBinding.googlePay.isClickable = false

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        val priceCents = (price + "00").toInt()

        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents.toLong())
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request), requireActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            // Value passed in AutoResolveHelper
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }

                    RESULT_CANCELED -> {
                        // The user cancelled the payment attempt
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }

                // Re-enables the Google Pay payment button.
                fragBinding.googlePay.isClickable = true
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson() ?: return

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData
                .getJSONObject("tokenizationData")
                .getString("token"))

        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString())
        }

    }

    private fun handleError(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
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