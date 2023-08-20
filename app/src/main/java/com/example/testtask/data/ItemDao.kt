package com.example.testtask.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM tbl_items")
    fun getItemsData(): Flow<List<ItemDataDB>>

    @Insert
    fun insert(item: ItemDataDB): Long

    @Query("DELETE FROM tbl_items WHERE id = :id")
    fun delete(id: Int): Int
}