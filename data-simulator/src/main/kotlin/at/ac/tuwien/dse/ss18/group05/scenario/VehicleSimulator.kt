package at.ac.tuwien.dse.ss18.group05.scenario

import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.RouteRecord
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.notifications.VehicleDataSender
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.ZonedDateTime
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Component
class VehicleSimulator(
    private val vehicles: List<Vehicle>,
    private val route: List<RouteRecord>,
    private val vehicleDataSender: VehicleDataSender
) {

    private val log = Logger.getLogger(this.javaClass.name)

    private val currentVehicleLocations = mutableMapOf<Vehicle, RouteRecord>()
    private lateinit var startTime: ZonedDateTime
    private var crashSimulated = false

    @Value("\${datasimulator.timeDelayInSecondsForEvent}")
    private val timeDelayInSecondsForEvent: Int = 2

    @Value("\${datasimulator.speedAfterEvent}")
    private val speedAfterEvent: Double = 50.0

    @PostConstruct
    fun simulate() {
        findStartingPointForVehicles()
        startTime = ZonedDateTime.now()
    }

    @Scheduled(fixedDelay = 1000)
    private fun handleVehicleSimulation() {
        calculateCurrentNotificationData()
        driveCarsToNextPoint()
    }

    private fun calculateCurrentNotificationData() {
        for (vehicle in vehicles) {
            val eventInfo = handleEventInformation(vehicle)
            val speed = handleSpeed(eventInfo, vehicle)
            val dataRecordCreator = VehicleDataRecordCreator(vehicle, currentVehicleLocations, eventInfo, speed)
            val dataRecord = dataRecordCreator.createNotificationForVehicle()
            vehicleDataSender.sendNotification(dataRecord)
        }
    }

    private fun handleEventInformation(vehicle: Vehicle): EventInformation {
        val timeBetweenStartAndNow = Math.abs(Duration.between(ZonedDateTime.now(), startTime).seconds)
        return if (!crashSimulated) {
            if (vehicle.crashing) {
                crashSimulated = true
                EventInformation.CRASH
            } else {
                EventInformation.NONE
            }
        } else {
            EventInformation.NONE
        }
    }

    private fun handleSpeed(eventInformation: EventInformation, vehicle: Vehicle): Double {
        return if (eventInformation != EventInformation.NONE) {
            //TODO does the car which produces the near crash event get a notification with the target speed?
            speedAfterEvent
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
        return if (currentIndex == route.size) {
            currentIndex
        } else {
            currentIndex + 1
        }
    }

    private fun findStartingPointForVehicles() {
        for (vehicle in vehicles) {
            val startingPoint = findStartingPointForVehicle(vehicle.startingAtKm)
            currentVehicleLocations[vehicle] = startingPoint
        }
    }

    private fun findStartingPointForVehicle(distance: Int): RouteRecord {
        return route.first { record -> record.distanceToStart >= distance }
    }

    fun simulateCrash() {
        crashSimulated = false
        startTime = ZonedDateTime.now()
    }

    fun setSpeedForVehicle(vehicleId: String, targetSpeed: Double?) {
        log.info("notification received vor vehicle with id: $vehicleId and target speed: $targetSpeed")
        val vehicle = currentVehicleLocations.map { (k, _) -> k }
                .find { vehicle -> vehicle.identificationNumber == vehicleId }
        if (vehicle != null && targetSpeed != null) {
            vehicle.speed = targetSpeed
        }
    }
}
