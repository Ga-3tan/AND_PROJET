package com.heigvd.and_projet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Authors : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane
 * Date : 10.06.2022
 */
class HostViewModelFactory(private val roomName: String, private val roomQuestion: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HostViewModel(roomName, roomQuestion) as T
    }
}