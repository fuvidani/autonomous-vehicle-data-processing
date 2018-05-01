package at.ac.tuwien.dse.ss18.group05.scenario
/* ktlint-disable no-wildcard-imports */

import at.ac.tuwien.dse.ss18.group05.dto.*

class VehicleDataRecordCreator(
    private val vehicle: Vehicle,
    private val currentVehicleLocations: Map<Vehicle, RouteRecord>,
    private val eventInformation: EventInformation,
    private val currentSpeed: Double
) {

    private val KM_TO_CM_CONVERSION = 1000 * 100

    fun createNotificationForVehicle(): VehicleDataRecord {
        val now = System.currentTimeMillis()
        val metaData = getMetaDataForVehicle()
        val sensorInformation = getSensorInformationForVehicle()
        return VehicleDataRecord(id = null, timestamp = now, metaData = metaData, sensorInformation = sensorInformation, eventInformation = eventInformation)
    }

    private fun getMetaDataForVehicle(): MetaData {
        return MetaData(vehicle.identificationNumber, vehicle.model)
    }

    private fun getSensorInformationForVehicle(): SensorInformation {
        val currentPosition = currentVehicleLocations[vehicle]
        if (currentPosition != null) {
            val gpsLocation = getGpsLocation(currentPosition)
            val proximityInformation = getProximityInformation(currentPosition)

            return SensorInformation(location = gpsLocation, proximityInformation = proximityInformation, passengers = vehicle.passengers, speed = currentSpeed)
        } else {
            throw IllegalStateException("there is no current position for the vehicle")
        }
    }

    private fun getGpsLocation(currentPosition: RouteRecord): GpsLocation {
        return GpsLocation(currentPosition.lat, currentPosition.lon)
    }

    private fun getProximityInformation(currentPosition: RouteRecord): ProximityInformation {
        val distanceToPreviousVehicle = findPreviousVehicle(currentPosition.distanceToStart)
        val distanceToNextVehicle = findNextVehicle(currentPosition.distanceToStart)
        return ProximityInformation(distanceToNextVehicle, distanceToPreviousVehicle)
    }

    private fun findNextVehicle(currentDistance: Double): Double {
        val nextDistance = currentVehicleLocations
                .map { (k, v) -> v.distanceToStart }
                .filter { distance -> distance > currentDistance }
                .min()
        return calculateDistanceBetweenCars(currentDistance, nextDistance)
    }

    private fun findPreviousVehicle(currentDistance: Double): Double {
        val previousDistance = currentVehicleLocations
                .map { (k, v) -> v.distanceToStart }
                .filter { distance -> distance < currentDistance }
                .max()
        return calculateDistanceBetweenCars(currentDistance, previousDistance)
    }

    private fun calculateDistanceBetweenCars(currentDistanceToStart: Double, otherDistanceToStart: Double?): Double {
        return if (otherDistanceToStart != null) {
            Math.abs(currentDistanceToStart - otherDistanceToStart) * KM_TO_CM_CONVERSION
        } else {
            Double.MAX_VALUE
        }
    }
}