package com.c22ps072.ficofit.ui.home.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.databinding.FragmentDashboardBinding
import com.c22ps072.ficofit.utils.Helpers.isVisible
import com.c22ps072.ficofit.utils.Helpers.setOrdinal
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

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
//        calendarViewSetup()

        binding.cardLeaderBeard.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionNavigationDashboardToLeaderBoardFragment())
        }
        binding.calendarContainer.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionNavigationDashboardToUnderDevelopmentDialog())
        }
    }

    private fun calendarViewSetup() {
        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    container.textView.text = day.date.dayOfMonth.toString()
                    if (day.owner == DayOwner.THIS_MONTH) {
                        container.textView.setTextColor(Color.DKGRAY)
                    } else {
                        container.textView.setTextColor(Color.LTGRAY)
                    }
                }
            }

            override fun create(view: View): DayViewContainer = DayViewContainer(view)

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentMonth = YearMonth.now()
            val firstMonth = currentMonth.minusMonths(10)
            val lastMonth = currentMonth.plusMonths(10)
            val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
            binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
            binding.calendarView.scrollToMonth(currentMonth)
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

        lifecycleScope.launch {
            viewModel.getUserCaloriesBurn().collect {
                binding.tvCaloriesBurn.text = it.toString()
            }
        }
    }

    class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendar_day_text)
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

    override fun onStart() {
        super.onStart()
        dashboardJob.start()
    }

    override fun onResume() {
        super.onResume()
        dashboardJob.start()
    }

    companion object {

    }
}