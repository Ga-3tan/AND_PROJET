package com.example.and_projet.ui.dashboard

import android.Manifest
import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.and_projet.ParticipantActivity
import com.example.and_projet.databinding.FragmentJoinBinding
import com.example.and_projet.models.ListRecord
import com.example.and_projet.utils.ListAdapter
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson


class JoinFragment : Fragment() {

    private lateinit var dashboardViewModel: JoinViewModel
    private var _binding: FragmentJoinBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this)[JoinViewModel::class.java]

        _binding = FragmentJoinBinding.inflate(inflater, container, false)

        val adapter = ListAdapter() { item ->
            Toast.makeText(view?.context, item.title, Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, ParticipantActivity::class.java)

            intent.putExtra("title", item.title)
            intent.putExtra("endPointId", item.endPointId)

            startActivity(intent)

            /*Nearby.getConnectionsClient(view?.context!!)
                .requestConnection("HOST", item.endPointId, connectionLifecycleCallback)
                .addOnSuccessListener(
                    OnSuccessListener { unused: Void? ->
                        // We successfully requested a connection. Now both sides
                        // must accept before the connection is established.
                        Log.i("DEBUG", "CONNECTED to ${item.endPointId}")
                    })
                .addOnFailureListener(
                    OnFailureListener { e: java.lang.Exception? ->
                        // Nearby Connections failed to request the connection.
                    })*/
        }
        _binding!!.joinRoomFoundList.adapter = adapter
        _binding!!.joinRoomFoundList.layoutManager = LinearLayoutManager(view?.context)

        // Adds list data observer
        dashboardViewModel.allRooms.observe(viewLifecycleOwner) { list ->
            list.let {
                adapter.setNotes(it)
            }
        }

        context?.let { startDiscovery(it) }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { startDiscovery(it) }
    }

    private fun startDiscovery(context: Context) {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        Nearby.getConnectionsClient(context)
            .startDiscovery("com.example.and_projet", endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { unused: Void? -> }
            .addOnFailureListener { e: Exception? -> }
    }

    private val endpointDiscoveryCallback: EndpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                // An endpoint was found. We request a connection to it.
                Log.i("DEBUG", "ENDPOINT FOUND $endpointId, ${info.endpointName}") // TODO

                dashboardViewModel.addRecord(info.endpointName, endpointId)
            }

            override fun onEndpointLost(endpointId: String) {
                // A previously discovered endpoint has gone away.
            }
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
                Nearby.getConnectionsClient(requireContext()).acceptConnection(endpointId, payloadCallback)

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


    // TODO PLACE THIS IN PARTICIPANT ACTIVITY AND ANOTHER IN HOST ACTIVITY
    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.i("DEBUG", "payload received " + String(payload.asBytes()!!, Charsets.UTF_8))
            val roomInfo = Gson().fromJson(String(payload.asBytes()!!, Charsets.UTF_8), ListRecord::class.java)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
            Log.i("DEBUG", "received from endpointId $endpointId")
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

    override fun onStart() {
        super.onStart()
        if (!hasPermissions(requireContext(), *REQUIRED_PERMISSIONS)) {
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
    @Deprecated("Deprecated in Java")
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
                Toast.makeText(requireContext(), "MISSING PERMISSION", Toast.LENGTH_LONG).show()
//                finish()
                return
            }
        }
        activity?.recreate()
    }

}