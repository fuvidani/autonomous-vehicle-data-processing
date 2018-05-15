package at.ac.tuwien.dse.ss18.group05.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

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

@Document(collection = "tracking_history")
data class VehicleDataRecord(
    @Id
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

data class ManufacturerDataRecord(
    val id: String?,
    val timestamp: Long,
    val vehicleIdentificationNumber: String,
    val model: String,
    val location: GpsLocation
)

fun VehicleDataRecord.toManufacturerDataRecord(): ManufacturerDataRecord {
    return ManufacturerDataRecord(
        this.id,
        this.timestamp,
        this.metaData.identificationNumber,
        this.metaData.model,
        this.sensorInformation.location
    )
}