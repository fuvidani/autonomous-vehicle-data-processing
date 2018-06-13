package at.ac.tuwien.dse.ss18.group05.scenario
/* ktlint-disable no-wildcard-imports */

import at.ac.tuwien.dse.ss18.group05.dto.*

/**
 * helper class with calculations of current position and information needed to post on messaging stream
 */
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
        val currentPosition = getCurrentPositionForVehicle()
        if (currentPosition != null) {
            val gpsLocation = getGpsLocation(currentPosition)
            val proximityInformation = getProximityInformation(currentPosition)
            return SensorInformation(location = gpsLocation, proximityInformation = proximityInformation, passengers = vehicle.passengers, speed = currentSpeed)
        } else {
            //this can only happen if the map map was not initialized properly or the equals/hash code method are not overwritten
            throw IllegalStateException("there is no current position for the vehicle")
        }
    }

    private fun getCurrentPositionForVehicle(): RouteRecord? {
        currentVehicleLocations.entries.forEach {
            if (it.key.identificationNumber == vehicle.identificationNumber) {
                return it.value
            }
        }
        return null
    }

    private fun getGpsLocation(currentPosition: RouteRecord): GpsLocation {
        return GpsLocation(currentPosition.lat, currentPosition.lon)
    }

    private fun getProximityInformation(currentPosition: RouteRecord): ProximityInformation {
        val distanceToPreviousVehicle = findPreviousVehicle(currentPosition.distanceToStart)
        val distanceToNextVehicle = findNextVehicle(currentPosition.distanceToStart)
        return ProximityInformation(distanceToNextVehicle, distanceToPreviousVehicle)
    }

    /**
     * streaming through all the current vehicle locations
     * filtering only those who are in front of the current car
     *
     * @param currentDistance the distance of the current vehicle
     */
    private fun findNextVehicle(currentDistance: Double): Double {
        val nextDistance = currentVehicleLocations
                .map { (k, v) -> v.distanceToStart }
                .filter { distance -> distance > currentDistance }
                .min()
        return calculateDistanceBetweenCars(currentDistance, nextDistance)
    }

    /**
     * streaming through all the current vehicle locations
     * filtering only those who are behind the current car
     *
     * @param currentDistance the distance of the current vehicle
     */
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