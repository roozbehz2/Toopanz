package com.roozbeh.toopan.appDataBase.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.roozbeh.toopan.app.MyApplication
import com.roozbeh.toopan.appDataBase.AppDatabase

 class ViewModelDB : ViewModel() {
     private val repository = AppDatabase.getInstance(MyApplication.getContext())?.historyDao()!!
     companion object {
         val instance = ViewModelDB()
     }
    val readData = repository.getAll()

    fun insertData(person: ItemsSearchNeshan){
//        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAll(person)
//        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<ItemsSearchNeshan>> {
        return repository.searchDatabase(searchQuery).asLiveData()
    }

}