package com.miner.game.domain

import com.miner.game.core.library.XY

data class Symbol (
    override var y: Float,
    override var x: Float,
    val symbolValue: Int
): XY