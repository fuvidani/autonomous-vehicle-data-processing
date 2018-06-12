package at.ac.tuwien.dse.ss18.group05.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * <h4>About this class</h4>

 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 1.0.0
 * @since 1.0.0
 */
data class GpsLocation(
    val lat: Double,
    val lon: Double
)

data class MetaData(
    val identificationNumber: String,
    val model: String
)

@Document(collection = "statistics")
data class AccidentReport(
    @Id
    val id: String?,
    val accidentId: String,
    val vehicleMetaData: MetaData,
    val location: GpsLocation,
    val passengers: Int,
    val timestampOfAccident: Long,
    val emergencyResponseInMillis: Long,
    val durationOfSiteClearingInMillis: Long
)