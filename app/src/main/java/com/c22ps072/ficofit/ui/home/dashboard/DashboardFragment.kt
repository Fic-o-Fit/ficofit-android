package com.c22ps072.ficofit.ui.home.dashboard

import android.annotation.SuppressLint
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
import com.c22ps072.ficofit.utils.Helpers.isVisible
import com.c22ps072.ficofit.utils.Helpers.setOrdinal
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

        initCollect()

        binding.cardLeaderBeard.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionNavigationDashboardToLeaderBoardFragment())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initCollect() {
        lifecycleScope.launchWhenCreated {
            setLoading(true)
            dashboardJob = launch {
                viewModel.getUserToken().collect { token ->
                    Log.e("Dashboard", "token : $token")
                    viewModel.getMyScore(token).collect { result ->
                        setLoading(false)
                        result.onSuccess { value ->
                            binding.tvCounterStreak.text = value.position.setOrdinal()
                            when (value.position) {
                                1 -> binding.ivTrophy.setImageResource(R.drawable.ic_trophy_gold)
                                2 -> binding.ivTrophy.setImageResource(R.drawable.ic_trophy_silver)
                                3 -> binding.ivTrophy.setImageResource(R.drawable.ic_trophy_bronze)
                                else -> binding.ivTrophy.setImageResource(R.drawable.ic_medal)
                            }
                            val subtitle = getString(R.string.your_current_position)
                            binding.tvSubtitle.text = "$subtitle (${value.score} pts)"
                            viewModel.saveUserName(value.name)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getUserName().collect {
                binding.tvName.text = it
            }
        }
    }

    private fun setLoading(state: Boolean){
        binding.apply {
            if (state) {
                viewLoading.isVisible(true)
                tvCounterStreak.visibility = View.INVISIBLE
                ivTrophy.visibility = View.INVISIBLE
                tvSubtitle.visibility = View.INVISIBLE
            }else {
                viewLoading.isVisible(false)
                tvCounterStreak.visibility = View.VISIBLE
                ivTrophy.visibility = View.VISIBLE
                tvSubtitle.visibility = View.VISIBLE
            }
        }
    }

    companion object {

    }
}