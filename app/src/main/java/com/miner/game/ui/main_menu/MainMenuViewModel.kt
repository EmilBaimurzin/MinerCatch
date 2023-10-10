package com.miner.game.ui.main_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainMenuViewModel: ViewModel() {
    private val _state = MutableLiveData(false)
    val state: LiveData<Boolean> = _state

    fun changeState(newState: Boolean) {
        _state.postValue(newState)
    }
}