package com.example.testtask.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tbl_items")
data class ItemDataDB(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val textValue: String
){

    companion object{
        fun List<ItemDataDB>.toItemData() = map {
            ItemData(it.id.toString(), it.textValue)
        }
    }

}