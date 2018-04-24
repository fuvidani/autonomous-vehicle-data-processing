package at.ac.tuwien.dse.ss18.group05.scenario

import at.ac.tuwien.dse.ss18.group05.dto.RouteRecord
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle

class VehicleSimulator(
    private val vehicles: List<Vehicle>,
    private val route: List<RouteRecord>
) {

    private val currentVehicleLocation = mutableMapOf<Vehicle, RouteRecord>()

    fun simulate() {
        findStartingPointForVehicles()
        driveCarsToNextPoint()
    }

    private fun driveCarsToNextPoint() {
        for (vehicle in vehicles) {
            val currentPosition = currentVehicleLocation.get(vehicle)

            val nextCar = findNextCar(currentPosition?.distanceToStart)
            val previousCar = findPreviousCar(currentPosition?.distanceToStart)

            val index = route.indexOf(currentPosition)
            val nextIndex = calculateNextLocationIndex(index)
            val nextPosition = route.get(nextIndex)

            currentVehicleLocation.apply { put(vehicle, nextPosition) }
        }

        Thread.sleep(1000)
        driveCarsToNextPoint()
    }

    private fun findNextCar(currentDistance: Double?) {
        val nextDistance = currentVehicleLocation
                .map { (k, v) -> v.distanceToStart }
                .filter { distance -> distance > currentDistance!! }
                .min()

        val nextCar = currentVehicleLocation.filter { (a, b) -> b.distanceToStart == nextDistance }
        println(nextCar)
    }

    private fun findPreviousCar(currentDistance: Double?) {
        val previousDistance = currentVehicleLocation
                .map { (k, v) -> v.distanceToStart }
                .filter { distance -> distance < currentDistance!! }
                .min()

        val previousCar = currentVehicleLocation.filter { (a, b) -> b.distanceToStart == previousDistance }
        println(previousCar)
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
            currentVehicleLocation.apply { put(vehicle, startingPoint) }
        }
    }

    private fun findStartingPointForVehicle(distance: Int): RouteRecord {
        return route
                .first { record -> record.distanceToStart >= distance }
    }
}
