package com.miner.game.ui.miner_game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.miner.game.core.library.GameViewModel
import com.miner.game.core.library.XYIMpl
import com.miner.game.core.library.random
import com.miner.game.domain.Symbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MinerGameViewModel : GameViewModel() {
    var isGoingLeft = false
    var isGoingRight = false
    var isGoingUp = false
    var isGoingDown = false

    private val _trigger = MutableStateFlow(true)
    val trigger = _trigger.asStateFlow()

    private val _pause = MutableLiveData(false)
    val pause: LiveData<Boolean> = _pause

    private val _symbols = MutableLiveData<List<Symbol>>(emptyList())
    val symbols: LiveData<List<Symbol>> = _symbols

    private val _goalSymbol = MutableLiveData<Int>()
    val goalSymbol: LiveData<Int> = _goalSymbol

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int> = _score

    private val _lives = MutableLiveData<Int>(3)
    val lives: LiveData<Int> = _lives

    fun changePauseState(value: Boolean) {
        _pause.postValue(value)
    }

    fun start(
        maxY: Int,
        maxX: Int,
        playerHeight: Int,
        playerWidth: Int,
        symbolSize: Int,
    ) {
        gameScope = CoroutineScope(Dispatchers.Default)
        letPlayerMove(maxY, maxX, playerHeight, playerWidth)
        checkSymbols(symbolSize, playerHeight, playerWidth, maxX, maxY)
    }

    fun setInitGoal() {
        val currentList = _symbols.value!!
        val filteredList = currentList.filter { it.symbolValue <= 6 }
        _goalSymbol.postValue(filteredList.random().symbolValue)
    }

    fun initPlayer(
        playerHeight: Int,
        playerWidth: Int,
        maxX: Int,
        maxY: Int
    ) {
        _playerXY.value = (XYIMpl(
            (maxX / 2 - playerWidth / 2).toFloat(),
            (maxY / 2 - playerHeight / 2).toFloat()
        ))
        _trigger.value = !_trigger.value
    }

    private fun checkSymbols(
        symbolSize: Int,
        playerHeight: Int,
        playerWidth: Int,
        maxX: Int,
        maxY: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                for (symbol in _symbols.value!!) {
                    val symbolX = symbol.x.toInt()..(symbol.x + symbolSize).toInt()
                    val symbolY = symbol.y.toInt()..(symbol.y + symbolSize).toInt()

                    val playerXYS = _playerXY.value

                    val playerX = playerXYS.x.toInt()..(playerXYS.x + playerWidth).toInt()
                    val playerY = playerXYS.y.toInt()..(playerXYS.y + playerHeight).toInt()

                    if (playerX.any { it in symbolX } && playerY.any { it in symbolY }) {

                        when (symbol.symbolValue) {
                            7 -> _score.postValue(_score.value!! + 5)
                            8 -> _lives.postValue(0)
                            else -> {
                                if (symbol.symbolValue == _goalSymbol.value!!) {
                                    _score.postValue(_score.value!! + 1)
                                } else {
                                    _score.postValue(_score.value!! - 1)
                                    _lives.postValue(_lives.value!! - 1)
                                }
                            }
                        }

                        updateList(maxX, maxY, playerWidth, playerHeight, symbolSize)
                        delay(2)
                        val currentList = _symbols.value!!
                        val filteredList = currentList.filter { it.symbolValue <= 6 }
                        _goalSymbol.postValue(filteredList.random().symbolValue)
                        _playerXY.value = (XYIMpl(
                            (maxX / 2 - playerWidth / 2).toFloat(),
                            (maxY / 2 - playerHeight / 2).toFloat()
                        ))
                        _trigger.value = !_trigger.value

                        break
                    }
                }
            }
        }
    }

    fun updateList(
        maxX: Int,
        maxY: Int,
        playerWidth: Int,
        playerHeight: Int,
        symbolSize: Int
    ) {
        val newList = mutableListOf<Symbol>()
        repeat(10) {
            val randomX = listOf(
                (0..(maxX / 2 - playerWidth / 2 - 10 - symbolSize)).random(),
                ((maxX / 2 + playerWidth / 2 + 10)..(maxX - symbolSize)).random(),
            ).random()

            val randomY =
                if (randomX < (maxX / 2 - playerWidth / 2 - 10 - symbolSize) || randomX > (maxX / 2 + playerWidth / 2 + 10)) {
                    0 random (maxY - symbolSize)
                } else {
                    listOf(
                        (0..(maxY / 2 - playerHeight / 2 - 10 - symbolSize)).random(),
                        ((maxY / 2 + playerHeight / 2 + 10)..maxY - symbolSize).random(),
                    ).random()
                }

            newList.add(Symbol(randomY.toFloat(), randomX.toFloat(), 1 random 8))
        }
        _symbols.postValue(newList)
    }

    private fun letPlayerMove(
        maxY: Int,
        maxX: Int,
        playerHeight: Int,
        playerWidth: Int,
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                val currentXY = _playerXY.value

                if (isGoingLeft && currentXY.x - 10f > 0) {
                    currentXY.x -= 10
                }

                if (isGoingRight && (currentXY.x + playerWidth) + 10f < maxX) {
                    currentXY.x += 10
                }

                if (isGoingUp && currentXY.y - 10f > 0) {
                    currentXY.y -= 10
                }

                if (isGoingDown && (currentXY.y + playerHeight) + 10f < maxY) {
                    currentXY.y += 10
                }
                _playerXY.value = (currentXY)
                _trigger.value = !_trigger.value
            }
        }
    }

    fun resetMove() {
        isGoingUp = false
        isGoingDown = false
        isGoingLeft = false
        isGoingRight = false
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}