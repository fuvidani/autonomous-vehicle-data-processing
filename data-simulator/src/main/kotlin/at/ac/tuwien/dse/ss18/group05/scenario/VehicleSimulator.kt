package at.ac.tuwien.dse.ss18.group05.scenario

import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.RouteRecord
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.notifications.VehicleDataSender
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.logging.Logger

/**
 * actual simulation implementation with the logic to drive the vehicles
 *
 */
@Component
class VehicleSimulator(
    private val vehicles: List<Vehicle>,
    private val route: List<RouteRecord>,
    private val vehicleDataSender: VehicleDataSender
) : CommandLineRunner {

    private val log = Logger.getLogger(this.javaClass.name)

    private val currentVehicleLocations = mutableMapOf<Vehicle, RouteRecord>()
    private var eventSimulated = false
    private var event = EventInformation.NONE

    @Value("\${datasimulator.speedAfterEvent}")
    private val speedAfterEvent: Double = 50.0

    @Value("\${simulation.pauseBetweenSimulationsInMS}")
    private val pauseBetweenSimulationsInMs: Long = 1000L

    @Volatile
    private var running = true

    override fun run(vararg args: String?) {
        simulate()
    }

    fun simulate() {
        log.info("starting simulation")
        setStartingPointForVehicles()
        simulateVehicleMovements()
    }

    private fun simulateVehicleMovements() {
        while (running) {
            driveCarsToNextPoint()
            calculateCurrentVehicleData()
            Thread.sleep(pauseBetweenSimulationsInMs)
        }
    }

    private fun calculateCurrentVehicleData() {
        for (vehicle in vehicles) {
            val eventInfo = handleEventInformation(vehicle)
            val speed = handleSpeed(eventInfo, vehicle)
            val dataRecordCreator = VehicleDataRecordCreator(vehicle, currentVehicleLocations, eventInfo, speed)
            val dataRecord = dataRecordCreator.createNotificationForVehicle()
            vehicleDataSender.sendVehicleDataRecord(dataRecord)
        }
    }

    private fun handleEventInformation(vehicle: Vehicle): EventInformation {
        return if (!eventSimulated) {
            if (vehicle.crashing) {
                eventSimulated = true
                event
            } else {
                EventInformation.NONE
            }
        } else {
            EventInformation.NONE
        }
    }

    private fun handleSpeed(eventInformation: EventInformation, vehicle: Vehicle): Double {
        return if (eventInformation != EventInformation.NONE) {
            speedAfterEvent //assume car breaks directly and does not wait for notification
        } else {
            vehicle.speed
        }
    }

    private fun driveCarsToNextPoint() {
        for (vehicle in vehicles) {
            val currentPosition = currentVehicleLocations[vehicle]
            val index = route.indexOf(currentPosition)
            val nextIndex = calculateNextLocationIndex(index)
            val nextPosition = route[nextIndex]
            currentVehicleLocations[vehicle] = nextPosition
        }
    }

    private fun calculateNextLocationIndex(currentIndex: Int): Int {
        //if on last position stay there
        return if (currentIndex == route.size - 1) {
            currentIndex
        } else {
            currentIndex + 1
        }
    }

    private fun setStartingPointForVehicles() {
        for (vehicle in vehicles) {
            val startingPoint = findStartingPointForVehicle(vehicle.startingAtKm)
            currentVehicleLocations[vehicle] = startingPoint
        }
    }

    /**
     * using the first operator on the route list to determine the index
     * of the location where the car should start based on its distance to the start point
     *
     * @param distance the distance to the starting point of the vehicle
     */
    private fun findStartingPointForVehicle(distance: Int): RouteRecord {
        return route.first { record -> record.distanceToStart >= distance }
    }

    /**
     * helper method to simulate an event from outside the simulator
     *
     * @param event the event to simulate
     */
    fun simulateEvent(event: EventInformation) {
        this.event = event
        eventSimulated = false
    }

    fun setSpeedForVehicle(vehicleId: String, targetSpeed: Double?) {
        log.info("notification received vor vehicle with id: $vehicleId")
        val vehicle = currentVehicleLocations.map { (k, _) -> k }
                .find { vehicle -> vehicle.identificationNumber == vehicleId }
        if (vehicle != null && targetSpeed != null) {
            vehicle.speed = targetSpeed
        }
    }

    /**
     * method stopping current simulation
     * resetting vehicles to starting point and then
     * starting simulation again
     */
    fun resetSimulation() {
        this.running = false
        setStartingPointForVehicles()
        this.running = true
    }
}
