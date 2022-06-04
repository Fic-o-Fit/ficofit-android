package com.c22ps072.ficofit.ui.home.leaderboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ps072.ficofit.data.source.remote.response.UserPoint
import com.c22ps072.ficofit.databinding.FragmentLeaderBoardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ui.home.leaderboard.UsersPointListAdapter

@AndroidEntryPoint
class LeaderBoardFragment : Fragment() {
    private var _binding: FragmentLeaderBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var usersPointListAdapter: UsersPointListAdapter

    private val viewModel: LeaderBoardViewModel by viewModels()
    private var leaderBoardJob: Job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLeaderBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().navigate(LeaderBoardFragmentDirections.actionLeaderBoardFragmentToNavigationDashboard())
        }

        usersPointListAdapter = UsersPointListAdapter()
        binding.rvUsersPoint.layoutManager = LinearLayoutManager(activity)
        binding.rvUsersPoint.adapter = usersPointListAdapter
//        usersPointListAdapter.setData(dummyData)
        retrieveData()
    }

    private fun retrieveData() {
        lifecycleScope.launchWhenCreated {
            leaderBoardJob = launch {
                viewModel.getUserToken().collect { token ->
                    viewModel.getLeaderBoard(token).collect { result ->
                        result.onSuccess { usersPointList ->
                            Log.e("LeaderBoardFragment", usersPointList.toString())
                            usersPointListAdapter.setData(usersPointList as ArrayList<UserPoint>)
                        }
                        result.onFailure {
                            Toast.makeText(activity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}