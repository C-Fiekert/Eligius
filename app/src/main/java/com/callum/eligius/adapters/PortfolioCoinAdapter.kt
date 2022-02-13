package com.callum.eligius.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callum.eligius.databinding.CardPortfolioBinding
import com.callum.eligius.databinding.CardPortfolioCoinBinding
import com.callum.eligius.models.CoinModel
import com.callum.eligius.models.PortfolioModel

interface CoinListener {
    fun onCoinClick(coin: CoinModel)
}

class PortfolioCoinAdapter constructor(private var coins: List<CoinModel>, private val listener: CoinListener) : RecyclerView.Adapter<PortfolioCoinAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPortfolioCoinBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val coin = coins[holder.adapterPosition]
        holder.bind(coin, listener)
    }

    override fun getItemCount(): Int = coins.size

    inner class MainHolder(val binding : CardPortfolioCoinBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coin: CoinModel, listener: CoinListener) {
            var coinNames = com.callum.eligius.helpers.coinList
            binding.coinName.text = coinNames[coin.name.toInt()]
            binding.youOwn.text = coin.amount
            binding.valueAmount.text = "€" + coin.value.toString()

            binding.root.setOnClickListener { listener.onCoinClick(coin) }
        }
    }
}