package de.uniks.codliners.stock_simulator.ui.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import de.uniks.codliners.stock_simulator.databinding.FragmentAchievementsBinding
import de.uniks.codliners.stock_simulator.ui.BaseFragment


/**
 * Fragment for the achievement ui.
 *
 * @author Lucas Held
 */
class AchievementsFragment : BaseFragment() {

    private val viewModel: AchievementsViewModel by viewModels {
        AchievementsViewModel.Factory(requireActivity().application)
    }

    private lateinit var binding: FragmentAchievementsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAchievementsBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.achievementsRecyclerView.adapter = AchievementsAdapter()
        binding.lifecycleOwner = this
        return binding.root
    }
}
