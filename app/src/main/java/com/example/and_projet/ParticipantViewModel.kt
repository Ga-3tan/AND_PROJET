package com.example.and_projet

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.and_projet.models.ListRecord
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Payload
import com.google.gson.Gson

class ParticipantViewModel : ViewModel() {
    var endpointId: String = ""

    fun sendAnswer(userName: String, answer: String, context: Context) {
        val data = Gson().toJson(ListRecord(userName, answer, ""))
        val payload = data.toByteArray(Charsets.UTF_8)
        Nearby.getConnectionsClient(context).sendPayload(
            endpointId,
            Payload.fromBytes(payload)
        )
    }
}