package ru.myitschool.nasa_bootcamp.ui.spacex

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.myitschool.nasa_bootcamp.R
import ru.myitschool.nasa_bootcamp.databinding.FragmentSpacexBinding
import ru.myitschool.nasa_bootcamp.ui.animation.animateIt

@AndroidEntryPoint
class SpaceXFragment : Fragment() {

    private lateinit var spaceXLaunchAdapter: SpaceXLaunchAdapter
    private val launchesViewModel: SpaceXViewModel by viewModels<SpaceXViewModelImpl>()

    private var _binding: FragmentSpacexBinding? = null
    private val binding get() = _binding!!

    private lateinit var onLaunchClick: SpaceXLaunchAdapter.OnLaunchClickListener


    internal var h: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        onLaunchClick = object : SpaceXLaunchAdapter.OnLaunchClickListener {
//            override fun onLaunchClick(launch: SxLaunchModel?, position: Int) {
//                Log.d("LAUNCH_CLICK_TAG", "Clicked at $position")
//            }
//        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        launchesViewModel.getViewModelScope().launch(Dispatchers.IO) {
            launchesViewModel.loadSpaceXLaunches()
        }
        _binding = FragmentSpacexBinding.inflate(inflater, container, false)
        spaceXLaunchAdapter = SpaceXLaunchAdapter()
        binding.launchesRecycle.adapter = spaceXLaunchAdapter
        launchesViewModel.getLaunchesList().observe(viewLifecycleOwner) {
            Log.d("SpaceX_Fragment_TAG", "Something changed in view model! $it")
            spaceXLaunchAdapter.submitList(it)
            Log.d("SpaceX_Fragment_TAG", "${spaceXLaunchAdapter.currentList}")
        }

        val animation = animateIt {
            animate(binding.spaceXLogo) animateTo {
                topOfItsParent(marginDp = 15f)
                leftOfItsParent(marginDp = 10f)
                scale(0.8f, 0.8f)
            }

            animate(binding.launches) animateTo {
                rightOf(binding.spaceXLogo, marginDp = 1f)
                sameCenterVerticalAs(binding.spaceXLogo)
            }

            animate(binding.explore) animateTo {
                rightOfItsParent(marginDp = 20f)
                sameCenterVerticalAs(binding.spaceXLogo)
            }

            animate(binding.background) animateTo {
                height(
                    resources.getDimensionPixelOffset(R.dimen.height),
                    horizontalGravity = Gravity.LEFT, verticalGravity = Gravity.TOP
                )
            }
        }

        binding.scrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            val percent = scrollY * 1f / v.maxScrollAmount
            animation.setPercent(percent)
        })

        val navController = findNavController()


        binding.explore.setOnClickListener {
            val action = SpaceXFragmentDirections.actionSpaceXFragmentToExploreFragment()
            navController.navigate(action)
        }

//        binding.launchesRecycle.setHasFixedSize(true)
        binding.launchesRecycle.layoutManager = GridLayoutManager(context, 1)


        return binding.root
    }
}