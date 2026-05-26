package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatorDao {
    // HISTORY queries
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("DELETE FROM calculation_history")
    suspend fun clearHistory()

    // FA_VORITES queries
    @Query("SELECT * FROM favorite_formulas")
    fun getAllFavoritesFlow(): Flow<List<FavoriteFormulaEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_formulas WHERE formulaId = :formulaId AND isFavorite = 1)")
    suspend fun isFormulaFavorite(formulaId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setFormulaFavorite(favorite: FavoriteFormulaEntity)

    @Query("DELETE FROM favorite_formulas WHERE formulaId = :formulaId")
    suspend fun removeFormulaFavorite(formulaId: String)
}
