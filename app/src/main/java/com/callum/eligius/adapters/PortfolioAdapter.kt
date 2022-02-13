package com.callum.eligius.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callum.eligius.databinding.CardPortfolioBinding
import com.callum.eligius.models.PortfolioModel

interface PortfolioListener {
    fun onPortfolioClick(portfolio: PortfolioModel)
}

class PortfolioAdapter constructor(private var portfolios: List<PortfolioModel>, private val listener: PortfolioListener) : RecyclerView.Adapter<PortfolioAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPortfolioBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val portfolio = portfolios[holder.adapterPosition]
        holder.bind(portfolio, listener)
    }

    override fun getItemCount(): Int = portfolios.size

    inner class MainHolder(val binding : CardPortfolioBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(portfolio: PortfolioModel, listener: PortfolioListener) {
            binding.portfolioName.text = portfolio.name + "'s Portfolio"
            binding.valueAmount.text = "€" + portfolio.value.toString()

            binding.root.setOnClickListener { listener.onPortfolioClick(portfolio) }
        }
    }
}