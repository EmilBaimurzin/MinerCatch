package com.miner.game.ui.main_menu

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.miner.game.core.library.GameFragment
import com.miner.game.databinding.FragmentMainMenuBinding
import com.miner.game.ui.miner_game.FragmentMinerGame
import com.miner.game.ui.other.MainActivity

class FragmentMainMenu : GameFragment<FragmentMainMenuBinding>(FragmentMainMenuBinding::inflate) {
    private val viewModel: MainMenuViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.no.setOnClickListener {
            viewModel.changeState(false)
        }

        binding.yes.setOnClickListener {
            requireActivity().finish()
        }

        binding.quit.setOnClickListener {
            viewModel.changeState(true)
        }

        binding.play.setOnClickListener {
            (requireActivity() as MainActivity).navigate(FragmentMinerGame())
        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }

        viewModel.state.observe(viewLifecycleOwner) {
            binding.quitLayout.isVisible = it
        }
    }
}