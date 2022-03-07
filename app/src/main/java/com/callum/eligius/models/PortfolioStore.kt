package com.callum.eligius.models

interface PortfolioStore {
    fun findAll() : List<PortfolioModel>
    fun findUserPortfolios(id: String): List<PortfolioModel>
    fun findOne(list: List<PortfolioModel>, id : String): PortfolioModel?
    fun create(portfolio: PortfolioModel)
}