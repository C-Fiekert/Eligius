package com.callum.eligius.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PortfolioModel(var id: String = "0", val name: String = "N/A", val value: Int = 0, val coins: MutableList<CoinModel>) : Parcelable {

}