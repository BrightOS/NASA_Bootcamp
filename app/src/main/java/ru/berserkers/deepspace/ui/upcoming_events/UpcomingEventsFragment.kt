package ru.berserkers.deepspace.ui.upcoming_events

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.berserkers.deepspace.databinding.FragmentUpcomingEventsBinding
import ru.berserkers.deepspace.utils.DimensionsUtil

/*
 * @author Danil Khairulin
 */
@AndroidEntryPoint
class UpcomingEventsFragment : Fragment() {
    private var _binding: FragmentUpcomingEventsBinding? = null
    private val binding get() = _binding!!

    private val launchesViewModel: UpcomingEventsViewModel by viewModels()
    private lateinit var upcomingEventsAdapter: UpcomingRecyclerAdapter

    private val handler = Handler(Looper.myLooper()!!)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpcomingEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.spaceLoading.startLoadingAnimation()
        initView()
        observeData()
    }

    private fun observeData() {
        launchesViewModel.getViewModelScope().launch {
            launchesViewModel.getUpcomingLaunches()
        }

        launchesViewModel.getUpcomingList().observe(viewLifecycleOwner) {
            upcomingEventsAdapter =
                UpcomingRecyclerAdapter(
                    requireContext(),
                    launchesViewModel.getUpcomingList().value!!
                )

            binding.recylcerUpcoming.adapter = upcomingEventsAdapter
            handler.postDelayed({ binding.spaceLoading.stopLoadingAnimation() }, DELAY)

        }
    }

    private fun initView() {
        binding.spaceLoading.startLoadingAnimation()

        DimensionsUtil.dpToPx(requireContext(), 10).let {
            DimensionsUtil.setMargins(
                view = binding.upcomingDescription,
                left = it,
                top = DimensionsUtil.getStatusBarHeight(resources) + it,
                right = it,
                bottom = 0
            )
        }
    }

    companion object {
        const val DELAY = 1000L
    }

    /*
        Plan a notification:

        val temp = NotificationCentre()
        temp.scheduleNotification(requireContext(), "Notification!", "Parse text", "2021-08-02-21-19-00")
    */
}
