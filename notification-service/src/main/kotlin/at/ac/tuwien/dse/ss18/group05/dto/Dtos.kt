package at.ac.tuwien.dse.ss18.group05.dto

/* ktlint-disable no-wildcard-imports */
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.*

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */

data class GpsLocation(val lat: Double, val lon: Double) : Serializable

enum class EventInformation {
    NEAR_CRASH,
    CRASH,
    NONE
}

enum class EmergencyServiceStatus {
    ARRIVED,
    AREA_CLEARED,
    UNKNOWN
}

data class IncomingVehicleNotification(
    val concernedFarAwayVehicles: Array<String>,
    val concernedNearByVehicles: Array<String>,
    val accidentId: String,
    val timestamp: Long,
    val location: GpsLocation,
    val emergencyServiceStatus: EmergencyServiceStatus,
    var specialWarning: Boolean?,
    var targetSpeed: Double?
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IncomingVehicleNotification

        if (!Arrays.equals(concernedFarAwayVehicles, other.concernedFarAwayVehicles)) return false
        if (!Arrays.equals(concernedNearByVehicles, other.concernedNearByVehicles)) return false
        if (accidentId != other.accidentId) return false
        if (timestamp != other.timestamp) return false
        if (location != other.location) return false
        if (emergencyServiceStatus != other.emergencyServiceStatus) return false
        if (specialWarning != other.specialWarning) return false
        if (targetSpeed != other.targetSpeed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(concernedFarAwayVehicles)
        result = 31 * result + Arrays.hashCode(concernedNearByVehicles)
        result = 31 * result + accidentId.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + emergencyServiceStatus.hashCode()
        result = 31 * result + (specialWarning?.hashCode() ?: 0)
        result = 31 * result + (targetSpeed?.hashCode() ?: 0)
        return result
    }
}

@Document(collection = "manufacturer_notifications")
data class ManufacturerNotification(
    @Id
    val id: String?,
    val timeStamp: Long,
    val vehicleIdentificationNumber: String,
    val manufacturerId: String,
    val model: String,
    val location: GpsLocation,
    val eventInfo: EventInformation,
    val accidentId: String
) : Serializable

@Document(collection = "emergency_notifications")
data class EmergencyServiceNotification(
    @Id
    val id: String?,
    val accidentId: String,
    val timeStamp: Long,
    val location: GpsLocation,
    val model: String,
    val passengers: Int,
    var status: EmergencyServiceStatus
) : Serializable

@Document(collection = "vehicle_notifications")
data class VehicleNotification(
    @Id
    val id: String?,
    val vehicleIdentificationNumber: String,
    val accidentId: String,
    val timestamp: Long,
    val location: GpsLocation,
    val emergencyServiceStatus: EmergencyServiceStatus,
    var specialWarning: Boolean?,
    var targetSpeed: Double?
) : Serializable {
    constructor(vehicleId: String, incomingVehicleNotification: IncomingVehicleNotification) : this(
            id = null,
            vehicleIdentificationNumber = vehicleId,
            accidentId = incomingVehicleNotification.accidentId,
            timestamp = incomingVehicleNotification.timestamp,
            location = incomingVehicleNotification.location,
            emergencyServiceStatus = incomingVehicleNotification.emergencyServiceStatus,
            specialWarning = incomingVehicleNotification.specialWarning,
            targetSpeed = incomingVehicleNotification.targetSpeed
    )
}
