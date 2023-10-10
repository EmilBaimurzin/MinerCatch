package com.miner.game.ui.ending

import android.os.Bundle
import android.view.View
import com.miner.game.core.library.GameFragment
import com.miner.game.databinding.FragmentEndingBinding
import com.miner.game.domain.SharedPrefs
import com.miner.game.ui.miner_game.FragmentMinerGame
import com.miner.game.ui.other.MainActivity

class FragmentEnding: GameFragment<FragmentEndingBinding>(FragmentEndingBinding::inflate) {
    private val sp by lazy {
        SharedPrefs(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.score.text = arguments?.getInt("SCORE").toString()
        binding.bestScore.text = sp.getBestScore().toString()

        binding.menu.setOnClickListener {
            (requireActivity() as MainActivity).navigateBack("main")
        }

        binding.restart.setOnClickListener {
            (requireActivity() as MainActivity).navigateBack("main")
            (requireActivity() as MainActivity).navigate(FragmentMinerGame())
        }
    }
}