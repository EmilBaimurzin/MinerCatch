package com.miner.game.domain

import android.content.Context

class SharedPrefs(private val context: Context) {
    private val sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE)

    fun getBestScore(): Int = sp.getInt("BEST", 0)
    fun setBestScore(score: Int) = sp.edit().putInt("BEST", score).apply()
}