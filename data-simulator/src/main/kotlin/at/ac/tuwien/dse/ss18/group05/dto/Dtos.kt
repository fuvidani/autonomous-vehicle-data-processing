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
    var startingAtKm: Int = 0,
    var passengers: Int = 1,
    var speed: Double = 50.0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vehicle

        if (identificationNumber != other.identificationNumber) return false
        if (manufacturerId != other.manufacturerId) return false
        if (model != other.model) return false
        if (crashing != other.crashing) return false
        if (startingAtKm != other.startingAtKm) return false
        if (passengers != other.passengers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = identificationNumber.hashCode()
        result = 31 * result + manufacturerId.hashCode()
        result = 31 * result + model.hashCode()
        result = 31 * result + crashing.hashCode()
        result = 31 * result + startingAtKm
        result = 31 * result + passengers
        return result
    }
}

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

data class EmergencyServiceMessage(
    val timestamp: Long,
    val accidentId: String,
    val status: EmergencyServiceStatus
)