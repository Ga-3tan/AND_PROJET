package com.example.and_projet

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.and_projet.databinding.ActivityHostBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import com.example.and_projet.models.ListRecord
import com.example.and_projet.utils.ListAdapter
import kotlin.text.Charsets.UTF_8


class HostActivity : AppCompatActivity() {

    lateinit var question: String
    lateinit var roomName: String

    private lateinit var hostViewModel: HostViewModel
    private lateinit var binding : ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hostViewModel = ViewModelProvider(this)[HostViewModel::class.java]

        question = intent.getStringExtra(ROOM_QUESTION_PARAMETER_KEY)!!
        roomName = intent.getStringExtra(ROOM_NAME_PARAMETER_KEY)!!

        title = roomName

        binding.hostQuestionContent.text = question

        val adapter = ListAdapter {}
        binding.hostAnswerList.adapter = adapter
        binding.hostAnswerList.layoutManager = LinearLayoutManager(this)

        // Adds list data observer
        hostViewModel.quizAnswers.observe(this) { list ->
            list.let {
                adapter.setRecords(it)
            }
        }

        startAdvertising()
    }

    companion object {
        const val ROOM_NAME_PARAMETER_KEY = "NAME_PARAMETER"
        const val ROOM_QUESTION_PARAMETER_KEY = "QUESTION_PARAMETER"
    }

    fun startServer() {

    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        Nearby.getConnectionsClient(applicationContext)
            .startAdvertising(
                roomName, "com.example.and_projet", connectionLifecycleCallback, advertisingOptions /* TODO CHANGE HOST TO "LOCALUSERNAME" */
            )
            .addOnSuccessListener(
                OnSuccessListener { unused: Void? -> })
            .addOnFailureListener(
                OnFailureListener { e: Exception? -> })
    }

    private val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                /*AlertDialog.Builder(this@HostActivity)
                    .setTitle("Accept connection to " + info.endpointName)
                    .setMessage("Confirm the code matches on both devices: " + info.authenticationDigits)
                    .setPositiveButton(
                        "Accept"
                    ) { dialog: DialogInterface?, which: Int ->  // The user confirmed, so we can accept the connection.
                        Nearby.getConnectionsClient(applicationContext)
                            .acceptConnection(endpointId, payloadCallback)
                    }
                    .setNegativeButton(
                        R.string.cancel
                    ) { dialog: DialogInterface?, which: Int ->  // The user canceled, so we should reject the connection.
                        Nearby.getConnectionsClient(applicationContext).rejectConnection(endpointId)
                    }
                    .setIcon(R.drawable.ic_dialog_alert)
                    .show()*/

                // Automatically accept the connection on both sides.
                Nearby.getConnectionsClient(this@HostActivity).acceptConnection(endpointId, payloadCallback)
                Log.i("DEBUG", "CONNECTED TO NEW DEVICE")
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        // We're connected! Can now start sending and receiving data.
                        Log.i("DEBUG", "ConnectionsStatusCodes.STATUS_OK")
                        sendRoomInfo(endpointId)
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
            }

        }

    private fun sendRoomInfo(endpointId: String) {
        val data = Gson().toJson(ListRecord(roomName, question, endpointId))
        val payload = data.toByteArray(UTF_8)
        Nearby.getConnectionsClient(this@HostActivity).sendPayload(
            endpointId,
            Payload.fromBytes(payload)
        )
    }

    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val data = Gson().fromJson(String(payload.asBytes()!!, UTF_8), ListRecord::class.java)
            hostViewModel.addAnswer(data.title, data.content, endpointId)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
            Log.i("DEBUG", endpointId)
        }
    }

    // PERMISSIONS
    private val REQUIRED_PERMISSIONS: Array<String> = arrayOf<String>(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        if (!hasPermissions(applicationContext, *REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false.  */
    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /** Handles user acceptance (or denial) of our permission request.  */
    @CallSuper
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return
        }
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(applicationContext, "MISSING PERMISSION", Toast.LENGTH_LONG).show()
                return
            }
        }
        recreate()
    }

}

