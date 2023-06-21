package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.util.FilterAsteroids

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = AsteroidListAdapter(AsteroidItemClickListener { asteroid ->
            viewModel.onAsteroidItemClicked(asteroid)
        })
        binding.asteroidRecycler.adapter = adapter
        viewModel.asteroids.observe(viewLifecycleOwner) { asteroids ->
            adapter.submitList(asteroids)
        }

        viewModel.pictureOfDay.observe(viewLifecycleOwner) { pictureOfDay ->
            pictureOfDay?.let {
                Picasso.with(requireContext())
                    .load(pictureOfDay.url)
                    .into(binding.activityMainImageOfTheDay)
                binding.activityMainImageOfTheDay.contentDescription = pictureOfDay.title
            }
        }

        viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner) { asteroid ->
            asteroid?.let {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.onAsteroidDetailsNavigated()
            }
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.onChangeFilter(
            when (item.itemId) {
                R.id.view_week_asteroids_menu -> FilterAsteroids.WEEK
                R.id.view_today_asteroids_menu -> FilterAsteroids.TODAY
                else -> FilterAsteroids.ALL
            }
        )
        return true
    }
}
