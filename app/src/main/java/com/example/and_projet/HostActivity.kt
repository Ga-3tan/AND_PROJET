package com.example.and_projet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.and_projet.databinding.ActivityHostBinding
import com.example.and_projet.databinding.ActivityMainBinding

class HostActivity : AppCompatActivity() {

    lateinit var question: String
    lateinit var roomName: String

    private lateinit var binding : ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        question = intent.getStringExtra(ROOM_QUESTION_PARAMETER_KEY)!!
        roomName = intent.getStringExtra(ROOM_NAME_PARAMETER_KEY)!!

        title = roomName

        binding.hostQuestionContent.text = question
    }

    companion object {
        const val ROOM_NAME_PARAMETER_KEY = "NAME_PARAMETER"
        const val ROOM_QUESTION_PARAMETER_KEY = "QUESTION_PARAMETER"
    }
}

