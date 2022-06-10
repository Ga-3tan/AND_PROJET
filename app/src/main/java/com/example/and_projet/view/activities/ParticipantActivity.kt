package com.example.and_projet.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.and_projet.databinding.ActivityParticipantBinding
import android.util.Log
import com.example.and_projet.viewmodel.ParticipantViewModel
import com.example.and_projet.model.ListRecord
import com.example.and_projet.viewmodel.HostViewModelFactory
import com.example.and_projet.viewmodel.ParticipantViewModelFactory
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson

/**
 * Authors : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane
 * Date : 10.06.2022
 */
class ParticipantActivity : AppCompatActivity() {
    private lateinit var binding : ActivityParticipantBinding
    private lateinit var participantViewModel: ParticipantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieves the endpoint id from previous intent
        val endPointId = intent.getStringExtra(ENDPOINT_ID_KEY)!!

        // Gets the view model instance
        val viewModelFactory = ParticipantViewModelFactory(endPointId)
        participantViewModel = ViewModelProvider(this, viewModelFactory)[ParticipantViewModel::class.java]

        // Sends answer payload on button click
        binding.participantAnswerButton.setOnClickListener {
            participantViewModel.sendAnswer(
                binding.participantName.text.toString(),
                binding.participantAnswerInput.text.toString(),
                this@ParticipantActivity)
            finish()
        }

        startDiscovery()
    }

    private fun startDiscovery() {
        Nearby.getConnectionsClient(this@ParticipantActivity)
            .requestConnection("HOST", participantViewModel.endpointId, connectionLifecycleCallback)
            .addOnSuccessListener(
                OnSuccessListener { unused: Void? ->
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                    Log.i("DEBUG", "CONNECTED to ${participantViewModel.endpointId} in the room")
                })
            .addOnFailureListener(
                OnFailureListener { e: java.lang.Exception? ->
                    // Nearby Connections failed to request the connection.
                })
    }

    private val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                // Automatically accept the connection on both sides.
                Nearby.getConnectionsClient(this@ParticipantActivity).acceptConnection(endpointId, payloadCallback)
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        // We're connected! Can now start sending and receiving data.
                        Log.i("DEBUG", "ConnectionsStatusCodes.STATUS_OK")
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        // The connection was rejected by one or both sides.
                        Log.i("DEBUG", "ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED")
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        // The connection broke before it was able to be accepted.
                        Log.i("DEBUG", "ConnectionsStatusCodes.STATUS_ERROR")
                    }
                    else -> {
                        // Unknown status code
                        Log.i("DEBUG", "Unknown status code")
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
                finish()
                Log.i("DEBUG", endpointId)
            }
        }

    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.i("DEBUG", "payload received " + String(payload.asBytes()!!, Charsets.UTF_8))
            val listRecord = Gson().fromJson(String(payload.asBytes()!!, Charsets.UTF_8), ListRecord::class.java)
            binding.participantAnswerQuestion.text = listRecord.content
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
            Log.i("DEBUG", "received from endpointId $endpointId")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
    }

    override fun onPause() {
        super.onPause()
        disconnect()
    }

    private fun disconnect() {
        Nearby.getConnectionsClient(this@ParticipantActivity).disconnectFromEndpoint(participantViewModel.endpointId)
    }

    companion object {
        const val ENDPOINT_ID_KEY = "ENDPOINT_ID"
    }
}