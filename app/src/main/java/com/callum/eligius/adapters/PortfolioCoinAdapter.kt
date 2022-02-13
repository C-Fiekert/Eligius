package com.callum.eligius.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callum.eligius.databinding.CardPortfolioBinding
import com.callum.eligius.databinding.CardPortfolioCoinBinding
import com.callum.eligius.models.CoinModel
import com.callum.eligius.helpers.coinList

interface CoinListener {
    fun onCoinClick(coin: CoinModel)
}

class PortfolioCoinAdapter constructor(private var coins: List<CoinModel>, private val filterString: String?, private val listener: CoinListener) : RecyclerView.Adapter<PortfolioCoinAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPortfolioCoinBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val coin = coins[holder.adapterPosition]

        if (filterString != null) {
            if (filterString?.let { coinList[coin.name].startsWith(filterString) }) {
                holder.bind(coin, listener)
            }
        } else {
            holder.bind(coin, listener)
        }
    }

    override fun getItemCount(): Int {
        var temp = coins.toMutableList()

        if (filterString != null) {
            for (coin in coins) {
                if (filterString?.let { !coinList[coin.name].startsWith(it) }) {
                    temp.remove(coin)
                }
            }
            return temp.size
        } else {
            return coins.size
        }
    }

    inner class MainHolder(val binding : CardPortfolioCoinBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coin: CoinModel, listener: CoinListener) {
            var coinNames = coinList
            binding.coinName.text = coinNames[coin.name]
            binding.youOwn.text = coin.amount
            // binding.valueAmount.text = "€" + coin.value.toString()

            binding.root.setOnClickListener { listener.onCoinClick(coin) }
        }
    }
}