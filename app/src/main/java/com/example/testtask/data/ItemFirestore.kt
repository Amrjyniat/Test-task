package com.example.testtask.data

import com.google.firebase.firestore.Exclude

data class ItemFirestore(
    @get:Exclude val id: String = "",
    val textValue: String = ""
)