package de.uniks.codliners.stock_simulator.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import de.uniks.codliners.stock_simulator.databinding.FragmentNewsBinding


class NewsFragment : Fragment() {

    private val viewModel: NewsViewModel by viewModels {
        val args = NewsFragmentArgs.fromBundle(arguments!!)
        val symbol = args.symbol
        NewsViewModel.Factory(activity!!.application, symbol)
    }

    private lateinit var binding: FragmentNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.newsRecyclerView.adapter = NewsAdapter(resources.configuration.locale)
        binding.lifecycleOwner = this

        viewModel.refresh()

        return binding.root
    }
}
