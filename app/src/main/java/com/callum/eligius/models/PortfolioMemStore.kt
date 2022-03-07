package com.callum.eligius.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber


var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class PortfolioMemStore : PortfolioStore {

    val portfolios = ArrayList<PortfolioModel>()
    val userPortfolios = ArrayList<PortfolioModel>()

    var auth = FirebaseAuth.getInstance()
    var db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app").reference

    override fun findAll(): List<PortfolioModel> {
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app/").reference
        db.child("portfolios").get().addOnSuccessListener {
            it.children.forEach {
                val id = it.child("id").value.toString()
                val name = it.child("name").value.toString()
                val value = it.child("value").value.toString().toInt()
                val coins: MutableList<CoinModel> = arrayListOf()

                if (it.child("coins").exists()) {
                    it.child("coins").children.forEach {
                        coins.add(CoinModel(it.child("id").value.toString(), it.child("name").value.toString().toInt(), it.child("amount").value.toString()))
                    }
                }
                if (findOne(portfolios, id) == null) {
                    portfolios.add(PortfolioModel(id, name, value, coins))
                }
            }
        }
        println(portfolios)
        return portfolios
    }

    override fun findUserPortfolios(id: String): List<PortfolioModel> {
        // userPortfolios.clear()
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app/").reference
        auth.currentUser?.let {
            db.child("users").child(it.uid).child("portfolios").get().addOnSuccessListener {
                for (portfolio in it.children) {
                    val id = portfolio.key.toString()

                    if (findOne(userPortfolios, id) == null) {
                        findOne(portfolios, id)?.let { it1 -> userPortfolios.add(it1) }
                    }
                }
                // Cycle through portfolios currently in the user list
                for (item in userPortfolios) {
                    var keep = false
                    // Cycle through portfolios in the user DB
                    for (portfolio in it.children) {
                        if (item.id == portfolio.key.toString()) {
                            keep = true
                        }
                    }
                    if (!keep) {
                        userPortfolios.remove(item)
                    }
                }

            }
        }

        println(userPortfolios)
        return userPortfolios
    }

    override fun findOne(list: List<PortfolioModel>, id: String): PortfolioModel? {
        return list.find { i -> i.id == id } //Find one entity by ID
    }

    override fun create(portfolio: PortfolioModel) {
        db = FirebaseDatabase.getInstance("https://eligius-29624-default-rtdb.europe-west1.firebasedatabase.app/").reference
        // portfolios.add(portfolio)
        db.child("portfolios").child(portfolio.id).setValue(portfolio).addOnCompleteListener { Timber.i("Portfolio added") }
        auth.currentUser?.let { db.child("users").child(it.uid).child("portfolios").child(portfolio.id).setValue(portfolio).addOnCompleteListener { Timber.i("User Portfolio added") } }

        logAll()
    }

    private fun logAll() {
        Timber.v("** Portfolios List **")
        portfolios.forEach { Timber.v("Portfolio ${it}") }
    }
}