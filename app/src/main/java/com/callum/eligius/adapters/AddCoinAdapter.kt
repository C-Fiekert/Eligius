package com.callum.eligius.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callum.eligius.databinding.CardAddCoinBinding
import com.callum.eligius.databinding.CardPortfolioBinding
import com.callum.eligius.models.PortfolioModel

class AddCoinAdapter(private var coins: ArrayList<Int>) : RecyclerView.Adapter<AddCoinAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardAddCoinBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val coin = coins[holder.adapterPosition]
        holder.bind(coin)
    }

    override fun getItemCount(): Int = coins.size

    inner class MainHolder(val binding : CardAddCoinBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coin: Int) {
            binding.coinNumber.text = "Coin ".plus(coin.toString())
            binding.coinSelected.minValue = 0
            binding.coinSelected.maxValue = 9
            binding.coinSelected.displayedValues = com.callum.eligius.helpers.coinList
        }
    }
}