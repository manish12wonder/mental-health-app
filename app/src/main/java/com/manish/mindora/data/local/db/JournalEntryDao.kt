package com.manish.mindora.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {

    @Query(
        """
        SELECT * FROM journal_entries
        ORDER BY createdAtEpochMillis DESC
        LIMIT 500
        """,
    )
    fun observeAll(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: JournalEntryEntity)
}
