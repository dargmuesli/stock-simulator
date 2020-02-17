package de.uniks.codliners.stock_simulator.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import de.uniks.codliners.stock_simulator.databinding.FragmentSearchBinding
import de.uniks.codliners.stock_simulator.ui.OnClickListener

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.searchResultRecyclerView.adapter = SearchResultAdapter(OnClickListener { searchResult ->
            val action = SearchFragmentDirections.actionNavigationSearchToShareFragment(searchResult.symbol)
            findNavController().navigate(action)
        })
        binding.lifecycleOwner = this
        return binding.root
    }
}
