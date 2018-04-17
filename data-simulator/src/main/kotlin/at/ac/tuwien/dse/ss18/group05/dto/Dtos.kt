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
@Deprecated(
    "This is just dummy notification for API test, use VehicleNotification instead.",
    level = DeprecationLevel.WARNING
)
data class Notification(
    val id: String,
    val concernedVehicles: List<String>,
    val message: String
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