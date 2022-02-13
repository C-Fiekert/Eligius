package com.callum.eligius.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callum.eligius.databinding.CardPortfolioBinding
import com.callum.eligius.models.PortfolioModel

interface PortfolioListener {
    fun onPortfolioClick(portfolio: PortfolioModel)
}

class PortfolioAdapter constructor(private var portfolios: List<PortfolioModel>, private val filterString: String?, private val listener: PortfolioListener) : RecyclerView.Adapter<PortfolioAdapter.MainHolder>() {

    // var temp: MutableList<PortfolioModel> = emptyList<PortfolioModel>() as MutableList<PortfolioModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPortfolioBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {

        val portfolio = portfolios[holder.adapterPosition]
        if (filterString != null) {
            if (filterString?.let { portfolio.name.startsWith(filterString) }) {
                holder.bind(portfolio, listener)
            }
        } else {
            holder.bind(portfolio, listener)
        }
    }

    override fun getItemCount(): Int {
        var temp = portfolios.toMutableList()

        if (filterString != null) {
            for (collection in portfolios) {
                if (filterString?.let { !collection.name.startsWith(it) }) {
                    temp.remove(collection)
                }
            }
            return temp.size
        } else {
            return portfolios.size
        }
    }

    inner class MainHolder(val binding : CardPortfolioBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(portfolio: PortfolioModel, listener: PortfolioListener) {
            binding.portfolioName.text = portfolio.name + "'s Portfolio"
            binding.valueAmount.text = "€" + portfolio.value.toString()

            binding.root.setOnClickListener { listener.onPortfolioClick(portfolio) }
        }
    }
}