package com.heigvd.and_projet.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heigvd.and_projet.model.ListRecord

/**
 * Authors : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane
 * Date : 10.06.2022
 */
class JoinViewModel : ViewModel() {
    val allRooms: LiveData<MutableList<ListRecord>> get() = _allRooms
    private val _allRooms = MutableLiveData(mutableListOf<ListRecord>())

    /**
     * Adds a room record to the list
     */
    fun addRecord(title: String, content: String, endPointId: String) {
        val ar = _allRooms.value!!.toMutableList()
        ar.add(ListRecord(title, content, endPointId))
        _allRooms.postValue(ar)
    }

    /**
     * Removes a given record from the list
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun removeRecord(endpointId: String) {
        val ar = _allRooms.value!!.toMutableList()
        ar.removeIf { it.endPointId == endpointId }
        _allRooms.postValue(ar)
    }
}