package com.callum.eligius.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callum.eligius.R
import com.callum.eligius.databinding.CardPortfolioBinding
import com.callum.eligius.databinding.CardPortfolioCoinBinding
import com.callum.eligius.helpers.Response
import com.callum.eligius.models.CoinModel
import com.callum.eligius.helpers.coinList

interface CoinListener {
    fun onCoinClick(coin: CoinModel)
}

class PortfolioCoinAdapter constructor(var coins: ArrayList<CoinModel>, private val filterString: String?, private val favourite: Boolean, private val listener: CoinListener) : RecyclerView.Adapter<PortfolioCoinAdapter.MainHolder>() {

    var myList: Response? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPortfolioCoinBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val coin = coins[holder.adapterPosition]

        if (filterString != null && !favourite) {
            if (filterString?.let { coinList[coin.name].startsWith(filterString) }) {
                holder.bind(coin, listener)
            }
        } else if (filterString == null && favourite) {
            if (coin.favourited) {
                holder.bind(coin, listener)
            }
        } else if (filterString != null && favourite) {
            if (filterString?.let { coinList[coin.name].startsWith(filterString) && coin.favourited}) {
                holder.bind(coin, listener)
            }
        } else {
            var coinNames = coinList
            holder.binding.coinName.text = coinNames[coin.name]
            holder.binding.youOwn.text = coin.amount
            if (coin.favourited) {
                holder.binding.favourite.setImageResource(android.R.drawable.star_big_on)
            } else {
                holder.binding.favourite.setImageResource(android.R.drawable.star_big_off)
            }

            var currentPrice = 0.0
            var percentage = 0.0

            if (coin.name == 0 && myList?.bitcoin?.eur != null) {
                currentPrice = (coin.amount.toDouble() * (myList?.bitcoin?.eur?.toDouble()!!))
                percentage = (coin.amount.toDouble() * (coin.value))
            } else if (coin.name == 1 && myList?.ethereum?.eur != null) {
                currentPrice = (coin.amount.toDouble() * (myList?.ethereum?.eur?.toDouble()!!))
                percentage = (coin.amount.toDouble() * (coin.value))
            } else if (coin.name == 2 && myList?.binancecoin?.eur != null) {
                currentPrice = (coin.amount.toDouble() * (myList?.binancecoin?.eur?.toDouble()!!))
                percentage = (coin.amount.toDouble() * (coin.value))
            } else if (coin.name == 3 && myList?.cardano?.eur != null) {
                currentPrice = (coin.amount.toDouble() * (myList?.cardano?.eur?.toDouble()!!))
                percentage = (coin.amount.toDouble() * (coin.value))
            } else if (coin.name == 4 && myList?.solana?.eur != null) {
                currentPrice = (coin.amount.toDouble().times(myList?.solana?.eur?.toDouble()!!))
                percentage = (coin.amount.toDouble() * (coin.value))
            } else if (coin.name == 5 && myList?.ripple?.eur != null) {
                currentPrice = (coin.amount.toDouble() * (myList?.ripple?.eur?.toDouble()!!))
                percentage = (coin.amount.toDouble() * (coin.value))
            } else if (coin.name == 6 && myList?.terra?.eur != null) {
                currentPrice = (coin.amount.toDouble() * (myList?.terra?.eur?.toDouble()!!))
                percentage = (coin.amount.toDouble() * (coin.value))
            } else if (coin.name == 7 && myList?.dogecoin?.eur != null) {
                currentPrice = (coin.amount.toDouble().times(myList?.dogecoin?.eur?.toDouble()!!))
                percentage = (coin.amount.toDouble() * (coin.value))
            } else if (coin.name == 8 && myList?.polkadot?.eur != null) {
                currentPrice = (coin.amount.toDouble() * (myList?.polkadot?.eur?.toDouble()!!))
                percentage = (coin.amount.toDouble() * (coin.value))
            } else if (coin.name == 9 && myList?.avalanche?.eur != null) {
                currentPrice = (coin.amount.toDouble() * (myList?.avalanche?.eur?.toDouble()!!))
                percentage = (coin.amount.toDouble() * (coin.value))
            }

            if (currentPrice >= percentage) {
                var increase = "%.2f".format(currentPrice).toFloat() - "%.2f".format(percentage).toFloat()
                var difference = ((increase/percentage) * 100)
                holder.binding.percentage.text = "%.1f".format(difference) + "%"
                holder.binding.percentage.setTextColor(Color.rgb(12, 124, 24))
            } else {
                var decrease = "%.2f".format(percentage).toFloat() - "%.2f".format(currentPrice).toFloat()
                var difference = ((decrease/percentage) * 100)
                holder.binding.percentage.text = "%.1f".format(difference) + "%"
                holder.binding.percentage.setTextColor(Color.RED)
            }

            holder.binding.valueAmount.text = "€" + "%.2f".format(currentPrice)
        }
    }

    override fun getItemCount(): Int {
        var temp = coins.toMutableList()

        if (filterString != null && !favourite) {
            for (coin in coins) {
                if (filterString?.let { !coinList[coin.name].startsWith(it) }) {
                    temp.remove(coin)
                }
            }
            return temp.size
        } else if (filterString == null && favourite) {
            for (coin in coins) {
                if (!coin.favourited) {
                    temp.remove(coin)
                }
            }
            return temp.size
        } else if (filterString != null && favourite) {
            for (coin in coins) {
                if (filterString?.let { !coinList[coin.name].startsWith(it) } && !coin.favourited) {
                    temp.remove(coin)
                }
            }
            return temp.size
        }
        else {
            return coins.size
        }
    }

    fun removeAt(position: Int) {
        coins.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getPrices(prices: Response) {
        myList = prices
        notifyDataSetChanged()
    }


    inner class MainHolder(val binding : CardPortfolioCoinBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coin: CoinModel, listener: CoinListener) {
            var coinNames = coinList
            binding.coinName.text = coinNames[coin.name]
            binding.youOwn.text = coin.amount
            if (coin.favourited) {
                binding.favourite.setImageResource(android.R.drawable.star_big_on)
            } else {
                binding.favourite.setImageResource(android.R.drawable.star_big_off)
            }
        }
    }
}