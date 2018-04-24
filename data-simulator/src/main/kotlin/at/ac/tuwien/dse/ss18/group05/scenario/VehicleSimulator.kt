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
        currentVehicleLocation.forEach { vehicle, routeRecord ->
            println("vehicle: $vehicle is on startingpoint $routeRecord")
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
