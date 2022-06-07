package com.c22ps072.ficofit.ui.home.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private var dashboardJob: Job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            dashboardJob = launch {
                viewModel.getUserToken().collect { token ->
                    Log.e("Dashboard", "token : $token")
                    viewModel.getMyScore(token).collect { result ->
                        result.onSuccess { value ->
                            binding.tvCounterStreak.text = value.position.toString()
                            when (value.position) {
                                1 -> binding.ivTrophy.setImageResource(R.drawable.ic_trophy_gold)
                                2 -> binding.ivTrophy.setImageResource(R.drawable.ic_trophy_silver)
                                3 -> binding.ivTrophy.setImageResource(R.drawable.ic_trophy_bronze)
                                else -> binding.ivTrophy.setImageResource(R.drawable.ic_medal)
                            }
                        }
                    }
                }
            }
        }

        binding.cardLeaderBeard.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionNavigationDashboardToLeaderBoardFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}