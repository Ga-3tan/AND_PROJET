package com.example.and_projet.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JoinViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is join Fragment"
    }
    val text: LiveData<String> = _text
}