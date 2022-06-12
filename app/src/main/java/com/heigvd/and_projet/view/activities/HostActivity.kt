package com.heigvd.and_projet.view.activities

import android.Manifest
import android.content.Context
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
import com.heigvd.and_projet.viewmodel.HostViewModel
import com.heigvd.and_projet.databinding.ActivityHostBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import com.heigvd.and_projet.model.ListRecord
import com.heigvd.and_projet.utils.ListAdapter
import com.heigvd.and_projet.viewmodel.HostViewModelFactory

/**
 * Authors : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane
 * Date : 10.06.2022
 */
class HostActivity : AppCompatActivity() {

    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1
    private val REQUIRED_PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private lateinit var hostViewModel: HostViewModel
    private lateinit var binding : ActivityHostBinding

    /**
     * Called when the activity is created
     * Retrieves the room data and displays the answers list
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieves the UI bindings
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieves room data from previous intent
        val roomQuestion = intent.getStringExtra(ROOM_QUESTION_PARAMETER_KEY)!!
        val roomName = intent.getStringExtra(ROOM_NAME_PARAMETER_KEY)!!

        // Gets the instance of the view model
        val viewModelFactory = HostViewModelFactory(roomName, roomQuestion)
        hostViewModel = ViewModelProvider(this, viewModelFactory)[HostViewModel::class.java]

        // Sets up the page title and UI
        title = hostViewModel.roomName
        binding.hostQuestionContent.text = hostViewModel.roomQuestion

        // Setup of the answers list
        val adapter = ListAdapter {}
        binding.hostAnswerList.adapter = adapter
        binding.hostAnswerList.layoutManager = LinearLayoutManager(this)

        // Adds list data observer
        hostViewModel.quizAnswers.observe(this) { list ->
            list.let {
                adapter.setRecords(it)
            }
        }

        // Starts the BLE room advertising
        startAdvertising()
    }

    /**
     * Checks permissions
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        if (!hasPermissions(applicationContext, *REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    /**
     * Handles user acceptance (or denial) of our permission request
     */
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

    /**
     * Verifies the allowed permissions
     */
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

    /**
     * Starts the advertising on BLE
     */
    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        Nearby.getConnectionsClient(this@HostActivity)
            .startAdvertising(
                hostViewModel.roomName, "and_projet", connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener(
                OnSuccessListener { unused: Void? -> })
            .addOnFailureListener(
                OnFailureListener { e: Exception? -> })
    }

    /**
     * This is the callback used during BLE connections
     * When a clients connects, the host sends the room info
     */
    private val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                // Automatically accept the connection on both sides.
                Nearby.getConnectionsClient(this@HostActivity).acceptConnection(endpointId, payloadCallback)
                Log.i("DEBUG", "CONNECTED TO NEW DEVICE")
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        // We're connected! Can now start sending and receiving data.
                        Log.i("DEBUG", "ConnectionsStatusCodes.STATUS_OK")
                        hostViewModel.sendRoomInfo(endpointId, this@HostActivity)
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

    /**
     * This is used as a callback to handle payloads
     */
    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val data = Gson().fromJson(String(payload.asBytes()!!, Charsets.UTF_8), ListRecord::class.java)
            hostViewModel.addAnswer(data.title, data.content, endpointId)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
            Log.i("DEBUG", endpointId)
        }
    }

    /**
     * Called on activity destroy
     */
    override fun onDestroy() {
        super.onDestroy()
        disconnect()
    }

    /**
     * Called on activity pause
     */
    override fun onPause() {
        super.onPause()
        disconnect()
    }

    /**
     * Called on activity stop
     */
    override fun onStop() {
        super.onStop()
        disconnect()
    }

    /**
     * Disconnects from all the BLE endpoints
     */
    private fun disconnect() {
        Nearby.getConnectionsClient(this@HostActivity).stopAllEndpoints()
    }

    companion object {
        const val ROOM_NAME_PARAMETER_KEY = "NAME_PARAMETER"
        const val ROOM_QUESTION_PARAMETER_KEY = "QUESTION_PARAMETER"
    }
}

