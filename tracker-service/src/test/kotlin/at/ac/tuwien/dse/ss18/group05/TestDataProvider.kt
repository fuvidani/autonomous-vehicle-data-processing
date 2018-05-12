package at.ac.tuwien.dse.ss18.group05

/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*

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

        fun testVehicleDataRecordAcura(): VehicleDataRecord {
            return VehicleDataRecord(
                null, System.currentTimeMillis(),
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
                null, System.currentTimeMillis(),
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

        fun testVehicleDataRecordTesla(): VehicleDataRecord {
            return VehicleDataRecord(
                null, System.currentTimeMillis(),
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
    }
}