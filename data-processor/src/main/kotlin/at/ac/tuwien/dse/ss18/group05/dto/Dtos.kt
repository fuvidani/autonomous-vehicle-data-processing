package at.ac.tuwien.dse.ss18.group05.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Arrays

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
    private val concernedFarAwayVehicles: Array<String>,
    private val concernedNearByVehicles: Array<String>,
    private val accidentId: String,
    private val timestamp: Long,
    private val location: GpsLocation,
    private val emergencyServiceStatus: EmergencyServiceStatus,
    private val specialWarning: Boolean?,
    private val targetSpeed: Double?
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
    val passengers: Int
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
    val emergencyResponseInMillis: Long,
    val durationOfSiteClearingInMillis: Long
)

fun VehicleDataRecord.toManufacturerNotification(
    manufacturerId: String,
    accidentId: String?
): ManufacturerNotification {
    return ManufacturerNotification(
        null,
        this.timestamp,
        this.metaData.identificationNumber,
        manufacturerId,
        this.metaData.model,
        this.sensorInformation.location,
        this.eventInformation,
        accidentId
    )
}

fun VehicleDataRecord.toDefaultLiveAccident(): LiveAccident {
    return LiveAccident(
        null,
        this.metaData,
        GeoJsonPoint(this.sensorInformation.location.lon, this.sensorInformation.location.lat),
        this.sensorInformation.passengers,
        this.timestamp,
        0,
        0
    )
}

fun VehicleDataRecord.toEmergencyServiceNotification(accidentId: String): EmergencyServiceNotification {
    return EmergencyServiceNotification(
        null,
        accidentId,
        this.timestamp,
        this.sensorInformation.location,
        this.metaData.model,
        this.sensorInformation.passengers
    )
}

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

fun LiveAccident.toAccidentReport(): AccidentReport {
    return AccidentReport(
        null,
        this.id!!,
        this.vehicleMetaData,
        GpsLocation(this.location.y, this.location.x),
        this.passengers,
        this.timestampOfServiceArrival!!.minus(this.timestampOfAccident),
        this.timestampOfSiteClearing!!.minus(this.timestampOfServiceArrival)
    )
}