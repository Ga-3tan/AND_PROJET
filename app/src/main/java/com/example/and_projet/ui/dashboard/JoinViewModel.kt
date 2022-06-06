package com.example.and_projet.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.and_projet.models.ListRecord

class JoinViewModel : ViewModel() {
    val allRooms: LiveData<MutableList<ListRecord>> get() = _allRooms
    private val _allRooms = MutableLiveData(mutableListOf(
        ListRecord("My Room", "Content"),
        ListRecord("Jean Jacques", "Super question"),
        ListRecord("Sophie", "Hello friends"),
        ListRecord("Marcel", "Quiz online")
    ))

    fun addRecord(title: String, content: String) {
        _allRooms.value?.add(ListRecord(title, content))
        //_allRooms.value = _allRooms.value
        _allRooms.postValue(_allRooms.value)
    }
}