package com.heigvd.and_projet.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heigvd.and_projet.model.ListRecord
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson

/**
 * Authors : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane
 * Date : 10.06.2022
 */
class HostViewModel(val roomName: String, val roomQuestion: String) : ViewModel() {
    val quizAnswers: LiveData<MutableList<ListRecord>> get() =  _quizAnswers
    private val _quizAnswers: MutableLiveData<MutableList<ListRecord>> = MutableLiveData(mutableListOf())

    /**
     * Adds a given answer to the list
     */
    fun addAnswer(username: String, content: String, endpointId: String) {
        val newList = _quizAnswers.value!!.toMutableList()
        if (newList.none{item -> item.endPointId == endpointId}) {
            newList.add(ListRecord(username, content, endpointId))
        }
        _quizAnswers.postValue(newList)
    }

    /**
     * Sends the current room data to the given BLE endpoint
     */
    fun sendRoomInfo(endpointId: String, context: Context) {
        val data = Gson().toJson(ListRecord(roomName, roomQuestion, endpointId))
        val payload = data.toByteArray(Charsets.UTF_8)
        Nearby.getConnectionsClient(context).sendPayload(
            endpointId,
            Payload.fromBytes(payload)
        )
    }
}