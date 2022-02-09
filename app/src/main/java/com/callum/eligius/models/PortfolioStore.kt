package com.callum.eligius.models

interface PortfolioStore {
    fun findAll() : List<PortfolioModel>
    fun findById(id: Long) : PortfolioModel?
    fun create(portfolio: PortfolioModel)
}