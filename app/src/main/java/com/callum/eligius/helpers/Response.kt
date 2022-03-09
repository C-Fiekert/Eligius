package com.callum.eligius.helpers

import com.google.gson.annotations.SerializedName

data class Response (
    val bitcoin: Price,
    val ethereum: Price,
    val binancecoin: Price,
    val cardano: Price,
    val solana: Price,
    val ripple: Price,
    @SerializedName("terra-luna")
    val terra: Price,
    val dogecoin: Price,
    val polkadot: Price,
    @SerializedName("avalanche-2")
    val avalanche: Price
)