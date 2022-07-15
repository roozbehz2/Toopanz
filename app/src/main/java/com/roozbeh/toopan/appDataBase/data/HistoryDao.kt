package com.roozbeh.toopan.appDataBase.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM itemsSearchNeshan")
    fun getAll(): Flow<List<ItemsSearchNeshan>>


    @Insert
    fun insertAll(vararg users: ItemsSearchNeshan)


    @Delete
    fun delete(user: ItemsSearchNeshan)


    @Query("SELECT * FROM itemsSearchNeshan WHERE address LIKE :searchQuery ")
    fun searchDatabase(searchQuery: String): Flow<List<ItemsSearchNeshan>>
}