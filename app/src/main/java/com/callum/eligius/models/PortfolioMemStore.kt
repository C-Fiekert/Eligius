package com.callum.eligius.models

import timber.log.Timber


var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class PortfolioMemStore : PortfolioStore {

    val portfolios = ArrayList<PortfolioModel>()

    override fun findAll(): List<PortfolioModel> {
        return portfolios
    }

    override fun findById(id:Long) : PortfolioModel? {
        val foundPortfolio: PortfolioModel? = portfolios.find { it.id == id }
        return foundPortfolio
    }

    override fun create(portfolio: PortfolioModel) {
        portfolio.id = getId()
        portfolios.add(portfolio)
        logAll()
    }

    private fun logAll() {
        Timber.v("** Portfolios List **")
        portfolios.forEach { Timber.v("Portfolio ${it}") }
    }
}