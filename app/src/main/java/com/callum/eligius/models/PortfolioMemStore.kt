package com.callum.eligius.models

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber


var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class PortfolioMemStore : PortfolioStore {

    val portfolios = ArrayList<PortfolioModel>()
    private lateinit var db: DatabaseReference

    override fun findAll(): List<PortfolioModel> {
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app/").reference
        db.child("Portfolios").get().addOnSuccessListener {
            it.children.forEach {
                val id = it.child("id").value.toString().toLong()
                val name = it.child("name").value.toString()
                val value = it.child("value").value.toString().toInt()
                val coins: MutableList<CoinModel> = arrayListOf()

                if (it.child("coins").exists()) {
                    it.child("coins").children.forEach {
                        coins.add(CoinModel(it.child("id").value.toString(), it.child("name").value.toString().toInt(), it.child("amount").value.toString()))
                    }
                }
                if (findById(id) in portfolios) {
                    println("Already in list")
                } else {
                    portfolios.add(PortfolioModel(id, name, value, coins))
                }
            }
        }
        return portfolios
    }

    override fun findById(id: Long) : PortfolioModel? {
        val foundPortfolio: PortfolioModel? = portfolios.find { it.id == id }
        return foundPortfolio
    }

    override fun create(portfolio: PortfolioModel) {
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app/").reference
        portfolio.id = getId()
        portfolios.add(portfolio)
        db.child("Portfolios").child(portfolio.id.toString()).setValue(portfolio).addOnCompleteListener { Timber.i("Portfolio added") }


        logAll()
    }

    private fun logAll() {
        Timber.v("** Portfolios List **")
        portfolios.forEach { Timber.v("Portfolio ${it}") }
    }
}