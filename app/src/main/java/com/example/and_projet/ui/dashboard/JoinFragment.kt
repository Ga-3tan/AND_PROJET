package com.example.and_projet.ui.dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.and_projet.ParticipantActivity
import com.example.and_projet.databinding.FragmentJoinBinding
import com.example.and_projet.utils.ListAdapter
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class JoinFragment : Fragment() {

    private lateinit var dashboardViewModel: JoinViewModel
    private var _binding: FragmentJoinBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var title: String
    private lateinit var content: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this)[JoinViewModel::class.java]

        _binding = FragmentJoinBinding.inflate(inflater, container, false)

        val adapter = ListAdapter { item ->
            val intent = Intent(activity, ParticipantActivity::class.java)
            intent.putExtra(ParticipantActivity.ENDPOINT_ID_KEY, item.endPointId)
            startActivity(intent)
        }

        _binding!!.joinRoomFoundList.adapter = adapter
        _binding!!.joinRoomFoundList.layoutManager = LinearLayoutManager(view?.context)

        // Adds list data observer
        dashboardViewModel.allRooms.observe(viewLifecycleOwner) { list ->
            list.let {
                adapter.setRecords(it)
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

                dashboardViewModel.addRecord(info.endpointName, "", endpointId)
            }

            override fun onEndpointLost(endpointId: String) {
                // A previously discovered endpoint has gone away.
                Log.i("DEBUG", "ENDPOINT LOST $endpointId")
                // dashboardViewModel.removeRecord(endpointId)
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