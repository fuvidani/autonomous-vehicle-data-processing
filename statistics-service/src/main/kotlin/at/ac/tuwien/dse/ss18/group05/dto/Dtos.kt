package at.ac.tuwien.dse.ss18.group05.dto

import org.springframework.data.annotation.Id

/**
 * <h4>About this class</h4>

 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 1.0.0
 * @since 1.0.0
 */
data class Statistics(
    @Id
    val id: String,
    val serialNumber: String,
    val model: String,
    val location: GpsLocation,
    val passengers: Int,
    val emergencyResponseInMillis: Double,
    val durationOfSiteClearing: Double
)

data class GpsLocation(
    val lat: String,
    val lon: String
)