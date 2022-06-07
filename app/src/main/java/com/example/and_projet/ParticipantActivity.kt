package com.example.and_projet

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.and_projet.databinding.ActivityParticipantBinding
import com.example.and_projet.models.ListRecord
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson

class ParticipantActivity : AppCompatActivity() {

    lateinit var title: String
    lateinit var endPointId: String

    private lateinit var binding: ActivityParticipantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_participant)

        binding = ActivityParticipantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = intent.getStringExtra("title")!!
        endPointId = intent.getStringExtra("endPointId")!!

        Nearby.getConnectionsClient(this@ParticipantActivity)
            .requestConnection("HOST", endPointId, connectionLifecycleCallback)
            .addOnSuccessListener(
                OnSuccessListener { unused: Void? ->
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                    Log.i("DEBUG", "CONNECTED to $endPointId in the room")
                })
            .addOnFailureListener(
                OnFailureListener { e: java.lang.Exception? ->
                    // Nearby Connections failed to request the connection.
                })
    }

    private val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                /*AlertDialog.Builder(context)
                    .setTitle("Accept connection to " + info.endpointName)
                    .setMessage("Confirm the code matches on both devices: " + info.authenticationDigits)
                    .setPositiveButton(
                        "Accept"
                    ) { dialog: DialogInterface?, which: Int ->  // The user confirmed, so we can accept the connection.
                        Nearby.getConnectionsClient(context!!)
                            .acceptConnection(endpointId, payloadCallback)
                    }
                    .setNegativeButton(
                        R.string.cancel
                    ) { dialog: DialogInterface?, which: Int ->  // The user canceled, so we should reject the connection.
                        Nearby.getConnectionsClient(context!!).rejectConnection(endpointId)
                    }
                    .setIcon(R.drawable.ic_dialog_alert)
                    .show()*/

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
                Log.i("DEBUG", endpointId)
            }
        }

    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.i("DEBUG", "payload received " + String(payload.asBytes()!!, Charsets.UTF_8))
            val listRecord = Gson().fromJson(String(payload.asBytes()!!, Charsets.UTF_8), ListRecord::class.java)
            binding.participantAnswerQuestion.text = listRecord.endPointId
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
            Log.i("DEBUG", "received from endpointId $endpointId")
        }
    }

    private fun disconnect(view: View) {
        Nearby.getConnectionsClient(this@ParticipantActivity).disconnectFromEndpoint(endPointId)
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect(View(this))
    }
}