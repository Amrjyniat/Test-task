package com.example.testtask.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtask.data.AppDatabase
import com.example.testtask.data.ItemData
import com.example.testtask.data.ItemDataDB
import com.example.testtask.data.ItemDataDB.Companion.toItemData
import com.example.testtask.data.ItemFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val itemsNode = Firebase.firestore.collection("items")
    private val itemDao = AppDatabase.getDatabase(context).itemDao()

    private val messageChannel = Channel<String>()
    val message = messageChannel.receiveAsFlow().shareIn(viewModelScope, SharingStarted.Lazily)

    val isFirestoreSource = MutableStateFlow(true)

    val itemInput = MutableStateFlow("")

    val items: StateFlow<List<ItemData>> = isFirestoreSource.flatMapLatest { isFirestore ->
        Log.i("TestCheck", "map isFirestoreSource: $isFirestore")
        if (isFirestore) {
            itemsNode.snapshots().map { snapshot ->
                snapshot.documents.map {
                    val item = it.toObject(ItemFirestore::class.java)
                    ItemData(it.id, item?.textValue.orEmpty())
                }
            }
        } else {
            itemDao.getItemsData().map { it.toItemData() }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, listOf())

    fun saveItem() {
        if (isFirestoreSource.value) {
            saveToFirestore()
        } else {
            saveToFLocalStorage()
        }
    }

    private fun saveToFirestore() {
        itemsNode
            .add(ItemFirestore(textValue = itemInput.value, id = ""))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    itemInput.value = ""
                } else {
                    messageChannel.trySend(it.exception?.localizedMessage.orEmpty())
                }
            }
    }

    private fun saveToFLocalStorage() {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccess = itemDao.insert(ItemDataDB(textValue = itemInput.value))
            if (isSuccess > -1) {
                itemInput.value = ""
            } else {
                messageChannel.trySend("Something went wrong")
            }
        }
    }

    fun deleteItem(itemId: String) {
        if (isFirestoreSource.value) {
            deleteItemFirestore(itemId)
        } else {
            deleteItemLocal(itemId)
        }
    }

    private fun deleteItemFirestore(itemId: String) {
        itemsNode.document(itemId)
            .delete()
            .addOnSuccessListener {
                messageChannel.trySend("Deleted success!")
            }
            .addOnFailureListener { e ->
                messageChannel.trySend(e.localizedMessage.orEmpty())
            }
    }

    private fun deleteItemLocal(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccess = itemDao.delete(itemId.toInt())
            if (isSuccess > -1) {
                messageChannel.trySend("Deleted success!")
            } else {
                messageChannel.trySend("Something went wrong")
            }
        }
    }

}

