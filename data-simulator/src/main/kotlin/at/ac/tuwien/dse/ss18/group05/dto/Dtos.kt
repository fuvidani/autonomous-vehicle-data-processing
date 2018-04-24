package at.ac.tuwien.dse.ss18.group05.dto

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */

data class Vehicle(
    var identificationNumber: String = "",
    var manufacturerId: String = "",
    var model: String = "",
    var crashing: Boolean = false,
    var startingAtKm: Int = 0
)

data class RouteRecord(
    val lat: Double,
    val lon: Double,
    val distanceToStart: Double
)

/***************************** OUTGOING *****************************/
data class VehicleDataRecord(
    val id: String?,
    val timestamp: Long,
    val metaData: MetaData,
    val sensorInformation: SensorInformation,
    val eventInformation: EventInformation
)

data class GpsLocation(val lat: Double, val lon: Double)

data class ProximityInformation(
    val distanceToVehicleFrontInCm: Double,
    val distanceToVehicleBehindInCm: Double
)

data class SensorInformation(
    val location: GpsLocation,
    val proximityInformation: ProximityInformation,
    val passengers: Int,
    val speed: Double
)

data class MetaData(
    val identificationNumber: String,
    val model: String
)

enum class EventInformation {
    NEAR_CRASH,
    CRASH,
    NONE
}

/***************************** INCOMING *****************************/
data class VehicleNotification(
    val id: String?,
    val accidentId: String,
    val timestamp: Long,
    val location: GpsLocation,
    val emergencyServiceStatus: EmergencyServiceStatus,
    val specialWarning: Boolean?,
    val targetSpeed: Double?
)

enum class EmergencyServiceStatus {
    ARRIVED,
    AREA_CLEARED,
    UNKNOWN
}