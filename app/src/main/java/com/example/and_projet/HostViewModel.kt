package com.example.and_projet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.and_projet.models.ListRecord

class HostViewModel : ViewModel() {
    val quizAnswers: LiveData<MutableList<ListRecord>> get() =  _quizAnswers
    private val _quizAnswers: MutableLiveData<MutableList<ListRecord>> = MutableLiveData(mutableListOf())

    fun addAnswer(username: String, content: String, endpointId: String) {
        val newList = _quizAnswers.value!!.toMutableList()
        if (newList.none{item -> item.endPointId == endpointId}) {
            newList.add(ListRecord(username, content, endpointId))
        }
        _quizAnswers.postValue(newList)
        println("ADDED ANSWER $content")
    }
}