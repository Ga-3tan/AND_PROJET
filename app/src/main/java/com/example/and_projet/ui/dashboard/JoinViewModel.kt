package com.example.and_projet.ui.dashboard

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.and_projet.models.ListRecord

class JoinViewModel : ViewModel() {
    val allRooms: LiveData<MutableList<ListRecord>> get() = _allRooms
    private val _allRooms = MutableLiveData(mutableListOf(ListRecord("Default title", "Default content", "Default ID")))

    fun addRecord(title: String, content: String, endPointId: String) {
        val ar = _allRooms.value!!.toMutableList() // copy is needed, BUG ?
        ar.add(ListRecord(title, content, endPointId))
        _allRooms.postValue(ar)
    }

    /*fun removeRecord(endPointId: String) {
        val ar = _allRooms.value!!.toMutableList() // copy is needed, BUG ?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ar.removeIf { it.endPointId == endPointId }
        }
        _allRooms.postValue(ar)
    }*/
}