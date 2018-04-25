package at.ac.tuwien.dse.ss18.group05.scenario

import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.RouteRecord
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.notifications.NotificationSender

class VehicleSimulator(
    private val vehicles: List<Vehicle>,
    private val route: List<RouteRecord>,
    private val notificationSender: NotificationSender
) {

    private val currentVehicleLocations = mutableMapOf<Vehicle, RouteRecord>()

    fun simulate() {
        findStartingPointForVehicles()
        handleVehicleSimulation()
    }

    private fun handleVehicleSimulation() {
        calculateCurrentNotificationData()
        driveCarsToNextPoint()
        Thread.sleep(1000)
        handleVehicleSimulation()
    }

    private fun calculateCurrentNotificationData() {
        for (vehicle in vehicles) {
            //TODO calculate event info and do something with speed
            val dataRecordCreator = VehicleDataRecordCreator(vehicle, currentVehicleLocations, EventInformation.NONE, 50.0)
            val dataRecord = dataRecordCreator.createNotificationForVehicle()
            notificationSender.sendNotification(dataRecord)
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
}
