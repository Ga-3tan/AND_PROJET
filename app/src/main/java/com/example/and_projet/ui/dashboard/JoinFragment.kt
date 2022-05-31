package com.example.and_projet.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.and_projet.databinding.FragmentJoinBinding
import com.example.and_projet.utils.ListAdapter

class JoinFragment : Fragment() {

    private var _binding: FragmentJoinBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[JoinViewModel::class.java]

        _binding = FragmentJoinBinding.inflate(inflater, container, false)

        val adapter = ListAdapter()
        _binding!!.joinRoomFoundList.adapter = adapter
        _binding!!.joinRoomFoundList.layoutManager = LinearLayoutManager(view?.context)

        // Adds list data observer
        dashboardViewModel.allRooms.observe(viewLifecycleOwner) { list ->
            list.let {
                adapter.setNotes(it)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}