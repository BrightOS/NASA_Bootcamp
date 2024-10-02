package ru.berserkers.deepspace.navigator.activities.planetsadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.berserkers.deepspace.R
import ru.berserkers.deepspace.databinding.PlanetItemBinding
import ru.berserkers.deepspace.navigator.rendertype.Planet

class PlanetsAdapter internal constructor(private val clickListener: OnPlanetClickListener) :
    ListAdapter<Planet, PlanetsAdapter.ViewHolder>(
        OperationDiffUtil()
    ) {

    interface OnPlanetClickListener {
        fun onPlanetClick(planet: Planet, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PlanetItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OperationDiffUtil : DiffUtil.ItemCallback<Planet>() {
        override fun areItemsTheSame(
            oldItem: Planet,
            newItem: Planet,
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Planet,
            newItem: Planet,
        ): Boolean =
            oldItem == newItem
    }

    class ViewHolder(
        val binding: PlanetItemBinding,
        private val onPlanetClick: OnPlanetClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var item: Planet

        fun bind(planet: Planet) {
            item = planet

            binding.iconPlanet.setImageDrawable(itemView.context.getDrawable(planet.id))
            binding.namePlanet.text = item.name


            with(binding.description) {
                when (planet) {
                    Planet.Mercury -> {
                        text = itemView.context.getString(R.string.mercury_description)
                    }
                    Planet.Venus -> {
                        text = itemView.context.getString(R.string.venus_description)
                    }
                    Planet.Sun -> {
                        text = itemView.context.getString(R.string.sun_description)
                    }
                    Planet.Mars -> {
                        text = itemView.context.getString(R.string.mars_description)
                    }
                    Planet.Jupiter -> {
                        text = itemView.context.getString(R.string.jupiter_description)
                    }
                    Planet.Saturn -> {
                        text = itemView.context.getString(R.string.saturn_description)
                    }
                    Planet.Uranus -> {
                        text = itemView.context.getString(R.string.uranus_description)
                    }
                    Planet.Neptune -> {
                        text = itemView.context.getString(R.string.neptune_description)
                    }
                    Planet.Pluto -> {
                        text = itemView.context.getString(R.string.pluto_description)
                    }
                    Planet.ISS -> {
                        text = itemView.context.getString(R.string.iss_description)
                    }

                    Planet.Moon -> Unit
                }
            }

            itemView.setOnClickListener {
                onPlanetClick.onPlanetClick(planet, adapterPosition)
            }
        }
    }
}
