package codingchallenge.adeel.nanoleafchallenge.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import codingchallenge.adeel.nanoleafchallenge.R
import codingchallenge.adeel.nanoleafchallenge.databinding.ActivityMainBinding
import codingchallenge.adeel.nanoleafchallenge.model.SimulationStatus
import codingchallenge.adeel.nanoleafchallenge.utils.getText
import codingchallenge.adeel.nanoleafchallenge.utils.hideKeyboard
import codingchallenge.adeel.nanoleafchallenge.utils.isValid
import codingchallenge.adeel.nanoleafchallenge.viewmodel.LightbulbViewModel

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }
    private val viewModel: LightbulbViewModel by viewModels()
    private val simulationResultAdapter by lazy { SimulationResultAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.simulationResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = simulationResultAdapter
        }

        viewBinding.runSimulationBtn.setOnClickListener {
            if (allFieldAreValid()) {
                //reset previous result and hide soft keyboard
                simulationResultAdapter.submitList(emptyList())
                it.hideKeyboard(this)

                with(viewBinding) {
                    val numberOfColors = numberOfColorsTextField.getText().toInt()
                    val numberOfLightbulbOfEachColor =
                        numberOfEachLightbulbColorsTextField.getText().toInt()

                    //calculate total number of lightbulbs
                    val totalNumberOfLightbulb = numberOfColors * numberOfLightbulbOfEachColor

                    val numberOfLightbulbToPick = numberOfLightbulbToPickTextField.getText().toInt()
                    val numberOfSimulations = numberOfTimesSimulationTextField.getText().toInt()

                    //Display total number of lightbulbs
                    numberOfLightbulbsLabel.text = getString(
                        R.string.total_number_of_lightbulbs,
                        totalNumberOfLightbulb.toString()
                    )

                    //Check if number of lightbulb to pick exceeds total number of lightbulbs available
                    if (numberOfLightbulbToPick > totalNumberOfLightbulb) {
                        numberOfLightbulbToPickTextField.error =
                            getString(R.string.lightbulb_to_pick_exceed_total_lightbulbs)
                        numberOfLightbulbToPickTextField.editText?.requestFocus()
                        return@setOnClickListener
                    } else {
                        numberOfLightbulbToPickTextField.isErrorEnabled = false
                    }

                    //run simulation on given inputs
                    viewModel.runSimulation(
                        numberOfColors,
                        totalNumberOfLightbulb,
                        numberOfSimulations,
                        numberOfLightbulbToPick
                    )
                }
            }
        }

        observeSimulationResult()
    }

    /**
     * Method to check if all inputs entered by user are valid
     * @return `true` if user entered everything else `false`
     */
    private fun allFieldAreValid(): Boolean {
        with(viewBinding) {
            if (!numberOfColorsTextField.getText().isValid()) {
                numberOfColorsTextField.error = getString(R.string.field_is_mandatory)
                numberOfColorsTextField.editText?.requestFocus()
                return false
            }

            if (!numberOfEachLightbulbColorsTextField.getText().isValid()) {
                numberOfEachLightbulbColorsTextField.error = getString(R.string.field_is_mandatory)
                numberOfColorsTextField.editText?.requestFocus()
                return false
            }

            if (!numberOfLightbulbToPickTextField.getText().isValid()) {
                numberOfLightbulbToPickTextField.error = getString(R.string.field_is_mandatory)
                numberOfColorsTextField.editText?.requestFocus()
                return false
            }

            if (!numberOfTimesSimulationTextField.getText().isValid()) {
                numberOfTimesSimulationTextField.error = getString(R.string.field_is_mandatory)
                numberOfColorsTextField.editText?.requestFocus()
                return false
            }
        }

        return true
    }

    private fun observeSimulationResult() {
        viewModel.simulationResultLiveData.observe(this) { simulationStatus ->
            when (simulationStatus) {
                is SimulationStatus.Loading -> {
                    viewBinding.simulationResultRecyclerView.isVisible = false
                    viewBinding.simulationProgressBar.isVisible = true
                    viewBinding.simulationProgress.isVisible = true

                    val simulationProgress = simulationStatus.currentSimulationNumber.toInt()
                    viewBinding.simulationProgressBar.progress = simulationProgress
                    viewBinding.simulationProgress.text = getString(R.string.simulation_progress, simulationProgress).plus("%")
                }

                is SimulationStatus.Success -> {
                    viewBinding.simulationResultRecyclerView.isVisible = true
                    viewBinding.simulationProgressBar.isVisible = false
                    viewBinding.simulationProgress.isVisible = false

                    simulationResultAdapter.submitList(simulationStatus.simulationResultList)
                }
            }
        }
    }
}