package com.heigvd.and_projet.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.heigvd.and_projet.view.activities.HostActivity
import com.heigvd.and_projet.databinding.FragmentCreateBinding

/**
 * Authors : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane
 * Date : 10.06.2022
 */
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
        // Retrieves the UI bindings
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // OnClick listener on the create room button
        binding.createRoomBtn.setOnClickListener {
            val roomName = binding.createRoomNameInputFieldEditText.text.toString()
            val question = binding.createRoomQuestionInputFieldEditText.text.toString()

            // Puts the room name and question in intent extras
            val intent = Intent(activity, HostActivity::class.java)
            intent.putExtra(HostActivity.ROOM_NAME_PARAMETER_KEY, roomName)
            intent.putExtra(HostActivity.ROOM_QUESTION_PARAMETER_KEY, question)

            startActivity(intent)
        }

        return binding.root
    }

    /**
     * Called when the fragment UI is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}