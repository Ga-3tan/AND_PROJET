package com.example.and_projet.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.and_projet.models.ListRecord

class JoinViewModel : ViewModel() {
    val allRooms: LiveData<MutableList<ListRecord>> get() = _allRooms
    private val _allRooms = MutableLiveData(mutableListOf<ListRecord>(ListRecord("AAA", "NULL")))

    fun addRecord(title: String, endPointId: String) {
        val ar = _allRooms.value!!.toMutableList() // copy is needed, BUG ?
        ar.add(ListRecord(title, endPointId))
        _allRooms.postValue(ar)
    }
}