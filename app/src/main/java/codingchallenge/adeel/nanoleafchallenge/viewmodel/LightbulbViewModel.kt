package codingchallenge.adeel.nanoleafchallenge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import codingchallenge.adeel.nanoleafchallenge.model.Lightbulb
import codingchallenge.adeel.nanoleafchallenge.model.SimulationResult
import codingchallenge.adeel.nanoleafchallenge.model.SimulationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LightbulbViewModel : ViewModel() {
    private val _simulationResultLiveData = MutableLiveData<SimulationStatus>()
    val simulationResultLiveData: LiveData<SimulationStatus> = _simulationResultLiveData

    private var simulationJob: Job? = null

    /**
     * Assigns random colors to lightbulbs.
     * Colors are expressed as integers
     */
    private fun assignColors(numberOfColors: Int, totalLightbulbs: Int): List<Lightbulb> {
        val lightbulbList = mutableListOf<Lightbulb>()
        for (i in 1..totalLightbulbs) {
            val color = (1..numberOfColors).random()
            lightbulbList.add(Lightbulb(color))
        }

        return lightbulbList
    }

    /**
     * Main function to execute the simulation. Repeats simulation up to number specified by user.
     * Responsible for assigning colors to lightbulbs, calculating estimated and actual number of unique colors.
     * Uses coroutines for better performance
     */
    fun runSimulation(
        numberOfColors: Int,
        totalLightbulbs: Int,
        numberOfSimulation: Int,
        numberOfLightbulbToPick: Int
    ) {
        //if previous simulation is running then cancel and start new one
        if (simulationJob?.isActive == true) {
            simulationJob?.cancel()
        }

        simulationJob = viewModelScope.launch {
            //Update UI to Loading
            emitSimulationStatus(SimulationStatus.Loading(0.0))

            //Assign colors to all lightbulbs
            val lightbulbsWithColor = assignColors(numberOfColors, totalLightbulbs)

            val uniqueColorsList = mutableListOf<Int>()
            val simulationResultList = mutableListOf<SimulationResult>()
            for (i in 1..numberOfSimulation) {
                //get progress in percentage
                emitSimulationStatus(SimulationStatus.Loading((i.toDouble() / numberOfSimulation) * 100))

                val uniqueColors = calculateUniqueColors(
                    numberOfLightbulbToPick,
                    lightbulbsWithColor
                )


                uniqueColorsList.add(uniqueColors)

                //Using Math.round to bring `double` to 2 decimal places
                val nextSimulationResult = Math.round(
                    (uniqueColorsList.sum().toDouble() / uniqueColorsList.size) * 100.0
                ) / 100.0

                simulationResultList.add(
                    SimulationResult(i, nextSimulationResult, uniqueColors)
                )
            }

            //I had 2 options here.
            //1. To update the simulation result on the UI as simulation progress or
            //2. To update the simulation result on the UI after all of the simulations have completed. Meanwhile, show user a progress bar to keep them notified of the progress.
            //I opted for option 2 because with option 1 in case of higher number of simulations (>3000), the UI was having too much work to do to keep updating the result and hence overall user experience was not good.
            _simulationResultLiveData.value =
                SimulationStatus.Success(simulationResultList)
        }
    }

    /**
     * Calculates the unique number of colors in a given list
     *
     * @param numberOfLightbulbToPick the numbers of lightbulb to pick as specified by users
     * @param lightbulbsWithColor list of lightbulbs with colors
     */
    private suspend fun calculateUniqueColors(
        numberOfLightbulbToPick: Int,
        lightbulbsWithColor: List<Lightbulb>
    ): Int {
        return withContext(Dispatchers.IO) {
            val lightbulbsPicked =
                lightbulbsWithColor.shuffled().take(numberOfLightbulbToPick)

            val uniqueColors = mutableSetOf<Int>()
            lightbulbsPicked.forEach {
                uniqueColors.add(it.lightbulbColor)
            }

            uniqueColors.size
        }
    }

    private fun emitSimulationStatus(status: SimulationStatus) {
        _simulationResultLiveData.value = status
    }
}