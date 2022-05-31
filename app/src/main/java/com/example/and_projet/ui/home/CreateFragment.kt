package com.example.and_projet.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.and_projet.HostActivity
import com.example.and_projet.databinding.FragmentCreateBinding

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(CreateViewModel::class.java)

        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.createRoomBtn.setOnClickListener {
            val roomName = binding.createRoomNameInputField.text.toString()
            val question = binding.createRoomQuestionInputField.text.toString()

            val intent = Intent(activity, HostActivity::class.java)
            intent.putExtra(HostActivity.ROOM_NAME_PARAMETER_KEY, roomName)
            intent.putExtra(HostActivity.ROOM_QUESTION_PARAMETER_KEY, question)

            startActivity(intent)

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}