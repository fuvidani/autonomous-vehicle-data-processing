package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.dto.VehicleLocation
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
class TestDataProvider {

    companion object {

        fun getTestVehicleLocation0(): VehicleLocation {
            return VehicleLocation(
                "JH4DB8590SS001561",
                GeoJsonPoint(16.3738189, 48.2081743) // Vienna
            )
        }

        fun getTestVehicleLocation1(): VehicleLocation {
            return VehicleLocation(
                "3GCPCSE03BG366866",
                GeoJsonPoint(16.3732133, 48.2089816) // Stephansplatz
            )
        }

        fun getTestVehicleLocation2(): VehicleLocation {
            return VehicleLocation(
                "4T4BE46K19R123050",
                GeoJsonPoint(16.3786159, 48.1739176) // Reumannplatz
            )
        }
    }
}