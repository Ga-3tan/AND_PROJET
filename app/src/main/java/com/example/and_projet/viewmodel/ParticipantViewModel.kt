package com.example.and_projet.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.and_projet.model.ListRecord
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Payload
import com.google.gson.Gson

/**
 * Authors : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane
 * Date : 10.06.2022
 */
class ParticipantViewModel(val endpointId: String) : ViewModel() {

    fun sendAnswer(userName: String, answer: String, context: Context) {
        val data = Gson().toJson(ListRecord(userName, answer, ""))
        val payload = data.toByteArray(Charsets.UTF_8)
        Nearby.getConnectionsClient(context).sendPayload(
            endpointId,
            Payload.fromBytes(payload)
        )
    }
}