package at.ac.tuwien.dse.ss18.group05.scenario

import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.RouteRecord
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.notifications.NotificationSender
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.ZonedDateTime
import javax.annotation.PostConstruct

@Component
class VehicleSimulator(
    private val vehicles: List<Vehicle>,
    private val route: List<RouteRecord>,
    private val notificationSender: NotificationSender
) {

    private val currentVehicleLocations = mutableMapOf<Vehicle, RouteRecord>()
    private lateinit var startTime: ZonedDateTime
    private var crashSimulated = false

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
            val speed = handleSpeed(eventInfo)
            val dataRecordCreator = VehicleDataRecordCreator(vehicle, currentVehicleLocations, eventInfo, speed)
            val dataRecord = dataRecordCreator.createNotificationForVehicle()
            notificationSender.sendNotification(dataRecord)
        }
    }

    private fun handleEventInformation(vehicle: Vehicle): EventInformation {
        val timeBetweenStartAndNow = Math.abs(Duration.between(ZonedDateTime.now(), startTime).seconds)
        return if (!crashSimulated && timeBetweenStartAndNow > 2) {
            if (vehicle.crashing) {
                crashSimulated = true
                EventInformation.NEAR_CRASH
            } else {
                EventInformation.NONE
            }
        } else {
            EventInformation.NONE
        }
    }

    private fun handleSpeed(eventInformation: EventInformation): Double {
        return if (eventInformation != EventInformation.NONE) {
            30.0
        } else {
            50.0
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
}
