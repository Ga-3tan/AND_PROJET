package com.example.and_projet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.and_projet.databinding.ActivityHostBinding
import com.example.and_projet.databinding.ActivityParticipantBinding

class ParticipantActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParticipantBinding
    private lateinit var participantViewModel: ParticipantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParticipantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        participantViewModel = ViewModelProvider(this)[ParticipantViewModel::class.java]

        // Retrieves the endpoint ID from extras
        participantViewModel.endpointId = intent.getStringExtra(ENDPOINT_ID_KEY)!!

        // Sends answer payload on button click
        binding.participantAnswerButton.setOnClickListener {
            participantViewModel.sendAnswer(binding.participantName.text.toString(), binding.participantAnswerInput.text.toString(), this@ParticipantActivity)
            finish()
            // TODO Close the BLE connection
        }
    }

    companion object {
        const val ENDPOINT_ID_KEY = "ENDPOINT_ID"
    }
}