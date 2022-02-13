package com.callum.eligius.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinModel(var id: String = "0", var name: Int = 0, var amount: String = "0") : Parcelable