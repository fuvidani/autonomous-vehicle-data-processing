package at.ac.tuwien.dse.ss18.group05.dto

/* ktlint-disable no-wildcard-imports */
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
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
data class Vehicle(
    @Id
    val identificationNumber: String,
    val manufacturerId: String,
    val model: String
)

data class VehicleDataRecord(
    val id: String?,
    val timestamp: Long,
    val metaData: MetaData,
    val sensorInformation: SensorInformation,
    val eventInformation: EventInformation
)

data class MetaData(
    val identificationNumber: String,
    val model: String
)

data class SensorInformation(
    val location: GpsLocation,
    val proximityInformation: ProximityInformation,
    val passengers: Int,
    val speed: Double
)

data class GpsLocation(val lat: Double, val lon: Double)

data class ProximityInformation(
    val distanceToVehicleFrontInCm: Double,
    val distanceToVehicleBehindInCm: Double
)

enum class EventInformation {
    NEAR_CRASH,
    CRASH,
    NONE
}

data class VehicleNotification(
    val concernedFarAwayVehicles: Array<String>,
    val concernedNearByVehicles: Array<String>,
    val accidentId: String,
    val timestamp: Long,
    val location: GpsLocation,
    val emergencyServiceStatus: EmergencyServiceStatus,
    val specialWarning: Boolean?,
    val targetSpeed: Double?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VehicleNotification

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

data class ManufacturerNotification(
    val id: String?,
    val timeStamp: Long,
    val vehicleIdentificationNumber: String,
    val manufacturerId: String,
    val model: String,
    val location: GpsLocation,
    val eventInfo: EventInformation,
    val accidentId: String?
)

data class EmergencyServiceNotification(
    val id: String?,
    val accidentId: String,
    val timeStamp: Long,
    val location: GpsLocation,
    val model: String,
    val passengers: Int,
    val status: EmergencyServiceStatus
)

@Document(collection = "locations")
data class VehicleLocation(
    @Id
    val vehicleIdentificationNumber: String,
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    val location: GeoJsonPoint
)

@Document(collection = "live_accidents")
data class LiveAccident(
    @Id
    val id: String?,
    val vehicleMetaData: MetaData,
    val location: GeoJsonPoint,
    val passengers: Int,
    val timestampOfAccident: Long,
    val timestampOfServiceArrival: Long?,
    val timestampOfSiteClearing: Long?
)

/**
 * Creates a new LiveAccident object from this and sets the timestamp of the
 * service's arrival to the provided one.
 *
 * @param timestampOfServiceArrival timestamp of the emergency service's arrival
 *
 * @return a new immutable LiveAccident object same as this with the exception being
 * the timestampOfServiceArrival attribute
 */
fun LiveAccident.withServiceArrival(timestampOfServiceArrival: Long): LiveAccident {
    return LiveAccident(
            this.id,
            this.vehicleMetaData,
            this.location,
            this.passengers,
            this.timestampOfAccident,
            timestampOfServiceArrival,
            this.timestampOfSiteClearing
    )
}

/**
 * Creates a new LiveAccident object from this and sets the timestamp of the event
 * when the accident's are has been cleared.
 *
 * @param timestampOfSiteClearing timestamp when the accident's are has been cleared
 *
 * @return a new immutable LiveAccident object same as this with the exception being
 * the timestampOfSiteClearing attribute
 */
fun LiveAccident.withSiteClearing(timestampOfSiteClearing: Long): LiveAccident {
    return LiveAccident(
            this.id,
            this.vehicleMetaData,
            this.location,
            this.passengers,
            this.timestampOfAccident,
            this.timestampOfServiceArrival,
            timestampOfSiteClearing
    )
}

data class AccidentReport(
    val id: String?,
    val accidentId: String,
    val vehicleMetaData: MetaData,
    val location: GpsLocation,
    val passengers: Int,
    val timestampOfAccident: Long,
    val emergencyResponseInMillis: Long,
    val durationOfSiteClearingInMillis: Long
)

data class ConcernedVehicles(
    val concernedFarAwayVehicles: List<String>,
    val concernedNearByVehicles: List<String>
)

/**
 * Transforms this VehicleDataRecord into a ManufacturerNotification, used when an accident
 * occurs.
 *
 * @param manufacturerId ID of the manufacturer the vehicle-record's vehicle belongs to
 * @param accidentId optional accident ID if the record is about a crash event; in case of
 * a near-crash event the accident ID may be null
 *
 * @return a valid, immutable ManufacturerNotification about the (near-) crash event
 */
fun VehicleDataRecord.toManufacturerNotification(
    manufacturerId: String,
    accidentId: String?
): ManufacturerNotification {
    return ManufacturerNotification(
            UUID.randomUUID().toString(),
            this.timestamp,
            this.metaData.identificationNumber,
            manufacturerId,
            this.metaData.model,
            this.sensorInformation.location,
            this.eventInformation,
            accidentId
    )
}

/**
 * Transforms this VehicleDataRecord into a default LiveAccident object, used when an accident
 * occurs.

 * @return a valid, immutable LiveAccident object containing the accident's information gathered up
 * to this point
 */
fun VehicleDataRecord.toDefaultLiveAccident(): LiveAccident {
    return LiveAccident(
            UUID.randomUUID().toString(),
            this.metaData,
            GeoJsonPoint(this.sensorInformation.location.lon, this.sensorInformation.location.lat),
            this.sensorInformation.passengers,
            this.timestamp,
            null,
            null
    )
}

/**
 * Transforms this VehicleDataRecord into an EmergencyServiceNotification, used when an accident
 * occurs.
 *
 * @param accidentId unique ID of the occurred accident
 *
 * @return a valid, immutable EmergencyServiceNotification to signal a new accident
 */
fun VehicleDataRecord.toEmergencyServiceNotification(accidentId: String): EmergencyServiceNotification {
    return EmergencyServiceNotification(
            UUID.randomUUID().toString(),
            accidentId,
            this.timestamp,
            this.sensorInformation.location,
            this.metaData.model,
            this.sensorInformation.passengers,
            EmergencyServiceStatus.UNKNOWN
    )
}

/**
 * Transforms this VehicleDataRecord into a VehicleNotification, used to notify vehicles about
 * accidents, emergency service updates.
 *
 * @param accidentId unique ID of the accident this notification corresponds to
 * @param emergencyServiceStatus the status of the emergency service
 * @param nearVehiclesList the list of vehicle IDs in the close perimeter of the accident
 * @param farVehiclesList the list of vehicle IDs in the wide perimeter of the accident
 * @param specialWarning optional flag that indicates whether there is a special warning projected on the accident's
 * scene. Only vehicles in close perimeter may be notified about this.
 * @param targetSpeed the objective speed all the vehicles close to the accident need to meet.
 * Only vehicles in close perimeter may be notified about this.
 *
 * @return a valid, immutable VehicleNotification
 */
fun VehicleDataRecord.toVehicleNotification(
    accidentId: String,
    emergencyServiceStatus: EmergencyServiceStatus,
    nearVehiclesList: List<String>,
    farVehiclesList: List<String>,
    specialWarning: Boolean?,
    targetSpeed: Double?
): VehicleNotification {
    return VehicleNotification(
            farVehiclesList.toTypedArray(),
            nearVehiclesList.toTypedArray(),
            accidentId,
            this.timestamp,
            this.sensorInformation.location,
            emergencyServiceStatus,
            specialWarning,
            targetSpeed
    )
}

/**
 * Transforms this closed accident into an AccidentReport object. Although this function can
 * be invoked even if the LiveAccident is still on-going, it does not make sense. Use only, if
 * the accident already has timestamps for the service arrival and the clearing of the site.
 *
 * @return a valid, immutable AccidentReport
 */
fun LiveAccident.toAccidentReport(): AccidentReport {
    return AccidentReport(
            UUID.randomUUID().toString(),
            this.id!!,
            this.vehicleMetaData,
            GpsLocation(this.location.y, this.location.x),
            this.passengers,
        this.timestampOfAccident,
            this.timestampOfServiceArrival!!.minus(this.timestampOfAccident),
            this.timestampOfSiteClearing!!.minus(this.timestampOfServiceArrival)
    )
}