package codingchallenge.adeel.nanoleafchallenge.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import codingchallenge.adeel.nanoleafchallenge.R
import codingchallenge.adeel.nanoleafchallenge.databinding.ItemSimulationResultBinding
import codingchallenge.adeel.nanoleafchallenge.model.SimulationResult

val diffUtil = object : DiffUtil.ItemCallback<SimulationResult>() {
    override fun areItemsTheSame(oldItem: SimulationResult, newItem: SimulationResult): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: SimulationResult, newItem: SimulationResult): Boolean {
        return oldItem == newItem
    }
}

class SimulationResultAdapter :
    ListAdapter<SimulationResult, SimulationResultAdapter.SimulationResultViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimulationResultViewHolder {
        val viewBinding = ItemSimulationResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SimulationResultViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: SimulationResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SimulationResultViewHolder(private val view: ItemSimulationResultBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(item: SimulationResult) = with(view) {
            estimatedNumbersOfColorsLabel.text = root.context.getString(
                R.string.estimated_number_of_unique_colors,
                item.estimatedNumberOfUniqueColors.toString()
            )
            numberOfColorsLabel.text = root.context.getString(
                R.string.number_of_unique_colors,
                item.simulationNumber,
                item.numberOfUniqueColors
            )
        }

    }
}
