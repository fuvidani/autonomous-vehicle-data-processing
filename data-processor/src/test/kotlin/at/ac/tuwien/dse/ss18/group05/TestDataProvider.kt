package at.ac.tuwien.dse.ss18.group05

/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import java.time.Duration

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

        fun testVehicleAcura(): Vehicle {
            return Vehicle("JH4DB8590SS001561", "Acura", "1995 Acura Integra")
        }

        fun testVehicleAudi(): Vehicle {
            return Vehicle("3GCPCSE03BG366866", "Audi", "Audi TT")
        }

        fun testVehicleTesla(): Vehicle {
            return Vehicle("4T4BE46K19R123050", "Tesla", "Tesla Model X")
        }

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

        fun testVehicleDataRecordAcura(): VehicleDataRecord {
            return VehicleDataRecord(
                null, 1526135809808,
                MetaData("JH4DB8590SS001561", "1995 Acura Integra"),
                SensorInformation(
                    GpsLocation(0.0, 0.0),
                    ProximityInformation(0.0, 0.0),
                    4,
                    50.0
                ),
                EventInformation.NONE
            )
        }

        fun testVehicleDataRecordAudi(): VehicleDataRecord {
            return VehicleDataRecord(
                null, 1526135828502,
                MetaData("3GCPCSE03BG366866", "Audi TT"),
                SensorInformation(
                    GpsLocation(0.0, 0.0),
                    ProximityInformation(0.0, 0.0),
                    4,
                    50.0
                ),
                EventInformation.NONE
            )
        }

        fun testVehicleDataRecordNearCrashTesla(location: GpsLocation): VehicleDataRecord {
            return VehicleDataRecord(
                null, 1526135842408,
                MetaData("4T4BE46K19R123050", "Tesla Model X"),
                SensorInformation(
                    location,
                    ProximityInformation(0.0, 0.0),
                    4,
                    50.0
                ),
                EventInformation.NEAR_CRASH
            )
        }

        fun testVehicleDataRecordNearCrashTesla(): VehicleDataRecord {
            return VehicleDataRecord(
                null, 1526135842408,
                MetaData("4T4BE46K19R123050", "Tesla Model X"),
                SensorInformation(
                    GpsLocation(0.0, 0.0),
                    ProximityInformation(0.0, 0.0),
                    4,
                    50.0
                ),
                EventInformation.NEAR_CRASH
            )
        }

        fun testVehicleDataRecordCrashTesla(): VehicleDataRecord {
            return VehicleDataRecord(
                null, 1526135842408,
                MetaData("4T4BE46K19R123050", "Tesla Model X"),
                SensorInformation(
                    GpsLocation(0.0, 0.0),
                    ProximityInformation(0.0, 0.0),
                    4,
                    50.0
                ),
                EventInformation.CRASH
            )
        }

        fun testEmergencyServiceMessageArrived(): EmergencyServiceMessage {
            return EmergencyServiceMessage(1526135842408, "onGoingAccident", EmergencyServiceStatus.ARRIVED)
        }

        fun testVehicleNotificationCrash(): VehicleNotification {
            return VehicleNotification(
                arrayOf("4T4BE46K19R123050", "3GCPCSE03BG366866"),
                arrayOf("JH4DB8590SS001561"),
                "onGoingAccident",
                1526135842408,
                GpsLocation(48.2089816, 16.3732133),
                EmergencyServiceStatus.UNKNOWN,
                true,
                25.0
            )
        }

        fun testEmergencyServiceNotification(): EmergencyServiceNotification {
            return EmergencyServiceNotification(
                null,
                "onGoingAccident",
                1526135842408,
                GpsLocation(48.2089816, 16.3732133),
                "Tesla Model X",
                4,
                EmergencyServiceStatus.UNKNOWN
            )
        }

        fun testManufacturerNotification(): ManufacturerNotification {
            return ManufacturerNotification(
                null,
                1526135842408,
                "4T4BE46K19R123050",
                "Tesla",
                "Tesla Model X",
                GpsLocation(48.2089816, 16.3732133),
                EventInformation.CRASH,
                "onGoingAccident"
            )
        }

        fun testAccidentReport(): AccidentReport {
            return AccidentReport(
                null,
                "onGoingAccident",
                MetaData("4T4BE46K19R123050", "Tesla Model X"),
                GpsLocation(48.2089816, 16.3732133),
                4,
                System.currentTimeMillis(),
                Duration.ofMinutes(15).toMillis(),
                Duration.ofMinutes(10).toMillis()
            )
        }

        fun testLiveAccident(): LiveAccident {
            return LiveAccident(
                null,
                MetaData("4T4BE46K19R123050", "Tesla Model X"),
                GeoJsonPoint(16.3732133, 48.2089816),
                4,
                1526242126726,
                null,
                null
            )
        }

        fun testLiveAccident(timestamp: Long): LiveAccident {
            return LiveAccident(
                "someOngoingAccident",
                MetaData("4T4BE46K19R123050", "Tesla Model X"),
                GeoJsonPoint(16.3732133, 48.2089816),
                4,
                timestamp,
                null,
                null
            )
        }
    }
}