package com.example.data.database

import kotlinx.coroutines.flow.Flow

class CalculatorRepository(private val dao: CalculatorDao) {
    val recentHistory: Flow<List<HistoryEntity>> = dao.getRecentHistory()
    val allFavoritesFlow: Flow<List<FavoriteFormulaEntity>> = dao.getAllFavoritesFlow()

    suspend fun insertHistory(expression: String, result: String) {
        dao.insertHistory(HistoryEntity(expression = expression, result = result))
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }

    suspend fun toggleFavorite(formulaId: String, makeFavorite: Boolean) {
        if (makeFavorite) {
            dao.setFormulaFavorite(FavoriteFormulaEntity(formulaId = formulaId, isFavorite = true))
        } else {
            dao.removeFormulaFavorite(formulaId)
        }
    }
}
