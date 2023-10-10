package com.miner.game.ui.miner_game

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.miner.game.R
import com.miner.game.core.library.GameFragment
import com.miner.game.databinding.FragmentMinerGameBinding
import com.miner.game.domain.SharedPrefs
import com.miner.game.ui.ending.FragmentEnding
import com.miner.game.ui.other.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentMinerGame :
    GameFragment<FragmentMinerGameBinding>(FragmentMinerGameBinding::inflate) {
    private val viewModel: MinerGameViewModel by viewModels()
    private val sp by lazy {
        SharedPrefs(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setJoystick()

        binding.play.setOnClickListener {
            viewModel.pauseState = false
            viewModel.changePauseState(false)
            viewModel.start(
                xy.y.toInt(),
                xy.x.toInt(),
                binding.player.height,
                binding.player.width,
                dpToPx(70)
            )
        }

        binding.pause.setOnClickListener {
            viewModel.pauseState = true
            viewModel.stop()
            viewModel.changePauseState(true)
        }

        binding.menu.setOnClickListener {
            (requireActivity() as MainActivity).navigateBack()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trigger.collect {
                    val playerXY = viewModel.playerXY.value
                    binding.player.x = playerXY.x
                    binding.player.y = playerXY.y
                }
            }
        }

        viewModel.pause.observe(viewLifecycleOwner) {
            binding.pauseLayout.isVisible = it
        }

        viewModel.lives.observe(viewLifecycleOwner) {
            binding.livesLayout.removeAllViews()
            repeat(it) {
                val liveView = ImageView(requireContext())
                liveView.layoutParams = LinearLayout.LayoutParams(dpToPx(20), dpToPx(20)).apply {
                    marginStart = dpToPx(2)
                    marginEnd = dpToPx(2)
                }
                liveView.setImageResource(R.drawable.live)
                binding.livesLayout.addView(liveView)
            }

            if (it == 0 && viewModel.gameState && !viewModel.pauseState) {
                viewModel.stop()
                viewModel.gameState = false
                if (sp.getBestScore() < viewModel.score.value!!) {
                    sp.setBestScore(viewModel.score.value!!)
                }
                (requireActivity() as MainActivity).navigate(FragmentEnding().apply {
                    arguments = Bundle().apply {
                        putInt("SCORE", viewModel.score.value!!)
                    }
                })
            }
        }

        viewModel.score.observe(viewLifecycleOwner) {
            binding.scores.text = it.toString()
        }

        viewModel.symbols.observe(viewLifecycleOwner) {
            binding.symbolsLayout.removeAllViews()
            it.forEach { symbol ->
                val symbolView = ImageView(requireContext())
                symbolView.layoutParams = ViewGroup.LayoutParams(dpToPx(70), dpToPx(70))
                symbolView.setImageResource(
                    when (symbol.symbolValue) {
                        1 -> R.drawable.symbol01
                        2 -> R.drawable.symbol02
                        3 -> R.drawable.symbol03
                        4 -> R.drawable.symbol04
                        5 -> R.drawable.symbol05
                        6 -> R.drawable.symbol06
                        7 -> R.drawable.bonus
                        else -> R.drawable.bomb
                    }
                )
                symbolView.x = symbol.x
                symbolView.y = symbol.y
                binding.symbolsLayout.addView(symbolView)
            }
        }

        viewModel.goalSymbol.observe(viewLifecycleOwner) {
            binding.goal.setImageResource(
                when (it) {
                    1 -> R.drawable.symbol01
                    2 -> R.drawable.symbol02
                    3 -> R.drawable.symbol03
                    4 -> R.drawable.symbol04
                    5 -> R.drawable.symbol05
                    6 -> R.drawable.symbol06
                    7 -> R.drawable.bonus
                    else -> R.drawable.bomb
                }
            )
        }

        startAction = {
            lifecycleScope.launch {
                if (viewModel.symbols.value!!.isEmpty()) {
                    viewModel.updateList(
                        xy.x.toInt(),
                        xy.y.toInt(),
                        binding.player.width,
                        binding.player.height,
                        dpToPx(70)
                    )
                    delay(4)
                    viewModel.setInitGoal()
                    viewModel.initPlayer(
                        binding.player.height,
                        binding.player.width,
                        xy.x.toInt(),
                        xy.y.toInt()
                    )
                }

                if (viewModel.gameState && !viewModel.pauseState) {
                    viewModel.start(
                        xy.y.toInt(),
                        xy.x.toInt(),
                        binding.player.height,
                        binding.player.width,
                        dpToPx(70)
                    )
                }
            }
        }
    }

    private fun setJoystick() {
        binding.joystick.setOnMoveListener { angle, strength ->
            viewModel.resetMove()
            if (strength == 0) {
                viewModel.resetMove()
            } else {
                when (angle) {
                    in 0..30 -> {
                        viewModel.resetMove()
                        viewModel.isGoingRight = true
                    }

                    in 31..60 -> {
                        viewModel.resetMove()
                        viewModel.isGoingRight = true
                        viewModel.isGoingUp = true
                    }

                    in 61..90 -> {
                        viewModel.resetMove()
                        viewModel.isGoingUp = true
                    }

                    in 91..120 -> {
                        viewModel.resetMove()
                        viewModel.isGoingUp = true
                    }

                    in 121..150 -> {
                        viewModel.resetMove()
                        viewModel.isGoingUp = true
                        viewModel.isGoingLeft = true
                    }

                    in 151..180 -> {
                        viewModel.resetMove()
                        viewModel.isGoingLeft = true
                    }

                    in 181..210 -> {
                        viewModel.resetMove()
                        viewModel.isGoingLeft = true
                    }

                    in 211..240 -> {
                        viewModel.resetMove()
                        viewModel.isGoingLeft = true
                        viewModel.isGoingDown = true
                    }

                    in 241..270 -> {
                        viewModel.resetMove()
                        viewModel.isGoingDown = true
                    }

                    in 271..300 -> {
                        viewModel.resetMove()
                        viewModel.isGoingDown = true
                    }

                    in 301..330 -> {
                        viewModel.resetMove()
                        viewModel.isGoingDown = true
                        viewModel.isGoingRight = true
                    }

                    in 331..359 -> {
                        viewModel.resetMove()
                        viewModel.isGoingRight = true
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }
}