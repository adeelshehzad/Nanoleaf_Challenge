package codingchallenge.adeel.nanoleafchallenge.model

sealed class SimulationStatus {
    data class Loading(val currentSimulationNumber: Double): SimulationStatus()
    data class Success(val simulationResultList: List<SimulationResult>): SimulationStatus()
}
