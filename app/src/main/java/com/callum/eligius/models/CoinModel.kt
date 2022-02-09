package com.callum.eligius.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinModel(var id: Long = 0, val name: String = "N/A", val amount: Int = 0, val value: Int = 0) : Parcelable