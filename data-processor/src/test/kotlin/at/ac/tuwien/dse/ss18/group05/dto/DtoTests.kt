package at.ac.tuwien.dse.ss18.group05.dto

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
class DtoTests {

    @Test
    fun liveAccidentTest1() {
        val accident = TestDataProvider.testLiveAccident()
        val newAccident = accident.withServiceArrival(1526242235256)
        Assert.assertEquals(null, accident.timestampOfServiceArrival)
        Assert.assertEquals(1526242235256, newAccident.timestampOfServiceArrival)
    }

    @Test
    fun liveAccidentTest2() {
        val accident = TestDataProvider.testLiveAccident()
        val newAccident = accident.withSiteClearing(1526242351774)
        Assert.assertEquals(null, accident.timestampOfSiteClearing)
        Assert.assertEquals(1526242351774, newAccident.timestampOfSiteClearing)
    }

    @Test
    fun liveAccidentTest3() {
        val accident = TestDataProvider.testLiveAccident()
        val newAccident = accident.withServiceArrival(1526242235256)
        val newestAccident = newAccident.withSiteClearing(1526242351774)
        Assert.assertEquals(null, accident.timestampOfServiceArrival)
        Assert.assertEquals(null, accident.timestampOfSiteClearing)
        Assert.assertEquals(1526242235256, newestAccident.timestampOfServiceArrival)
        Assert.assertEquals(1526242351774, newestAccident.timestampOfSiteClearing)
    }

    @Test
    fun vehicleDataRecordToManufacturerNotificationTest() {
        val record = TestDataProvider.testVehicleDataRecordAudi()
        val notification = record.toManufacturerNotification("Audi", "someAccidentId")
        Assert.assertEquals("Audi", notification.manufacturerId)
        Assert.assertEquals("someAccidentId", notification.accidentId)
        Assert.assertEquals(record.timestamp, notification.timeStamp)
        Assert.assertEquals(record.metaData.identificationNumber, notification.vehicleIdentificationNumber)
        Assert.assertEquals(record.metaData.model, notification.model)
        Assert.assertEquals(record.sensorInformation.location, notification.location)
        Assert.assertEquals(record.eventInformation, notification.eventInfo)
    }

    @Test
    fun vehicleDataRecordToDefaultLiveAccidentTest() {
        val record = TestDataProvider.testVehicleDataRecordNearCrashTesla()
        val accident = record.toDefaultLiveAccident()
        Assert.assertEquals(null, accident.id)
        Assert.assertEquals(record.metaData, accident.vehicleMetaData)
        Assert.assertEquals(record.sensorInformation.location.lon, accident.location.x, 0.0)
        Assert.assertEquals(record.sensorInformation.location.lat, accident.location.y, 0.0)
        Assert.assertEquals(record.sensorInformation.passengers, accident.passengers)
        Assert.assertEquals(record.timestamp, accident.timestampOfAccident)
        Assert.assertEquals(null, accident.timestampOfServiceArrival)
        Assert.assertEquals(null, accident.timestampOfSiteClearing)
    }

    @Test
    fun vehicleDataRecordToEmergencyServiceNotification() {
        val record = TestDataProvider.testVehicleDataRecordAcura()
        val notification = record.toEmergencyServiceNotification("someAccidentId")
        Assert.assertEquals(null, notification.id)
        Assert.assertEquals("someAccidentId", notification.accidentId)
        Assert.assertEquals(record.timestamp, notification.timeStamp)
        Assert.assertEquals(record.sensorInformation.location, notification.location)
        Assert.assertEquals(record.metaData.model, notification.model)
        Assert.assertEquals(record.sensorInformation.passengers, notification.passengers)
    }

    @Test
    fun vehicleDataRecordToVehicleNotificationTest() {
        val record = TestDataProvider.testVehicleDataRecordAudi()
        val notification = record.toVehicleNotification(
            "someAccidentId", EmergencyServiceStatus.UNKNOWN,
            listOf("JH4DB8590SS001561"), listOf("4T4BE46K19R123050", "3GCPCSE03BG366866"), true, 25.0
        )
        Assert.assertArrayEquals(
            arrayOf("4T4BE46K19R123050", "3GCPCSE03BG366866"),
            notification.concernedFarAwayVehicles
        )
        Assert.assertArrayEquals(arrayOf("JH4DB8590SS001561"), notification.concernedNearByVehicles)
        Assert.assertEquals("someAccidentId", notification.accidentId)
        Assert.assertEquals(record.timestamp, notification.timestamp)
        Assert.assertEquals(record.sensorInformation.location, notification.location)
        Assert.assertEquals(EmergencyServiceStatus.UNKNOWN, notification.emergencyServiceStatus)
        Assert.assertTrue(notification.specialWarning!!)
        Assert.assertEquals(25.0, notification.targetSpeed!!, 0.0)
    }

    @Test
    fun liveAccidentToAccidentReportTest() {
        val instant = Instant.now()
        val now = instant.toEpochMilli()
        val accident = TestDataProvider.testLiveAccident(now)
        val newAccident = accident.withServiceArrival(instant.plus(10, ChronoUnit.MINUTES).toEpochMilli())
        val newestAccident = newAccident.withSiteClearing(instant.plus(25, ChronoUnit.MINUTES).toEpochMilli())
        val report = newestAccident.toAccidentReport()
        Assert.assertNull(report.id)
        Assert.assertEquals(newestAccident.id, report.accidentId)
        Assert.assertEquals(newestAccident.vehicleMetaData, report.vehicleMetaData)
        Assert.assertEquals(newestAccident.location.y, report.location.lat, 0.0)
        Assert.assertEquals(newestAccident.location.x, report.location.lon, 0.0)
        Assert.assertEquals(newestAccident.passengers, report.passengers)
        Assert.assertEquals(1000 * 60 * 10, report.emergencyResponseInMillis)
        Assert.assertEquals(1000 * 60 * 15, report.durationOfSiteClearingInMillis)
    }
}