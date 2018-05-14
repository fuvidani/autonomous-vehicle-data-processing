package at.ac.tuwien.dse.ss18.group05.messaging.notification

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import at.ac.tuwien.dse.ss18.group05.messaging.sender.Sender
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
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
@RunWith(SpringRunner::class)
class NotifierTest {

    private lateinit var notifier: INotifier
    @MockBean
    private lateinit var sender: Sender

    @Before
    fun setUp() {
        notifier = Notifier(sender)
    }

    @Test
    fun testNotifyManufacturerShouldInvokeSenderWithCorrectNotification_NearCrash() {
        // NEAR_CRASH
        val dataRecord = TestDataProvider.testVehicleDataRecordTesla()
        val expectedNotification = ManufacturerNotification(
            null,
            dataRecord.timestamp, dataRecord.metaData.identificationNumber, "Tesla", dataRecord.metaData.model,
            dataRecord.sensorInformation.location, EventInformation.NEAR_CRASH, null
        )
        notifier.notifyManufacturer(dataRecord, null, "Tesla")
        Mockito.verify(sender).sendMessage(expectedNotification, "notifications.manufacturer")
    }

    @Test
    fun testNotifyManufacturerShouldInvokeSenderWithCorrectNotification_Crash() {
        // CRASH
        val dataRecord = TestDataProvider.testVehicleDataRecordCrashTesla()
        val expectedNotification = ManufacturerNotification(
            null,
            dataRecord.timestamp, dataRecord.metaData.identificationNumber, "Tesla", dataRecord.metaData.model,
            dataRecord.sensorInformation.location, EventInformation.CRASH, "someGeneratedAccidentId"
        )
        notifier.notifyManufacturer(dataRecord, "someGeneratedAccidentId", "Tesla")
        Mockito.verify(sender).sendMessage(expectedNotification, "notifications.manufacturer")
    }

    @Test
    fun testNotifyEmergencyServiceShouldInvokeSenderWithCorrectNotification_Crash() {
        val dataRecord = TestDataProvider.testVehicleDataRecordCrashTesla()
        val expectedNotification = EmergencyServiceNotification(
            null,
            "someGeneratedAccidentId",
            dataRecord.timestamp,
            dataRecord.sensorInformation.location,
            dataRecord.metaData.model,
            dataRecord.sensorInformation.passengers
        )
        notifier.notifyEmergencyService(dataRecord, "someGeneratedAccidentId")
        Mockito.verify(sender).sendMessage(expectedNotification, "notifications.ems")
    }

    @Test
    fun testNotifyStatisticsServiceShouldInvokeSenderWithCorrectNotification() {
        val now = Instant.now()
        val accident = TestDataProvider.testLiveAccident(now.toEpochMilli())
        val accidentWithArrival = accident.withServiceArrival(now.plus(5, ChronoUnit.MINUTES).toEpochMilli())
        val resolvedAccident = accidentWithArrival.withSiteClearing(now.plus(15, ChronoUnit.MINUTES).toEpochMilli())
        val expectedReport = AccidentReport(
            null,
            accident.id!!,
            resolvedAccident.vehicleMetaData,
            GpsLocation(resolvedAccident.location.y, resolvedAccident.location.x),
            resolvedAccident.passengers,
            1000 * 60 * 5,
            1000 * 60 * 10
        )
        notifier.notifyStatisticsService(resolvedAccident)
        Mockito.verify(sender).sendMessage(expectedReport, "statistics.report")
    }

    @Test
    fun testNotifyVehiclesOfNewAccidentShouldInvokeSenderWithCorrectNotification() {
        val dataRecord = TestDataProvider.testVehicleDataRecordCrashTesla()
        val concernedVehicles = ConcernedVehicles(listOf("1", "2", "3"), listOf("4", "5", "6"))
        val expectedNotification = VehicleNotification(
            concernedVehicles.concernedFarAwayVehicles.toTypedArray(),
            concernedVehicles.concernedNearByVehicles.toTypedArray(),
            "someGeneratedAccidentId",
            dataRecord.timestamp,
            dataRecord.sensorInformation.location,
            EmergencyServiceStatus.UNKNOWN,
            true,
            10.0
        )
        notifier.notifyVehiclesOfNewAccident(dataRecord, "someGeneratedAccidentId", concernedVehicles)
        Mockito.verify(sender).sendMessage(expectedNotification, "notifications.vehicle")
    }

    @Test
    fun testNotifyVehiclesOfAccidentUpdateShouldInvokeSenderWithCorrectNotification_Arrival() {
        val onGoingAccident = TestDataProvider.testLiveAccident(System.currentTimeMillis())
        val concernedVehicles = ConcernedVehicles(listOf("1", "2", "3"), listOf("4", "5", "6"))
        val time = System.currentTimeMillis()
        val updatedAccident = onGoingAccident.withServiceArrival(time)
        val expectedNotification = VehicleNotification(
            concernedVehicles.concernedFarAwayVehicles.toTypedArray(),
            concernedVehicles.concernedNearByVehicles.toTypedArray(),
            "someOngoingAccident",
            time,
            GpsLocation(updatedAccident.location.y, updatedAccident.location.x),
            EmergencyServiceStatus.ARRIVED,
            true,
            10.0
        )
        notifier.notifyVehiclesOfAccidentUpdate(updatedAccident, EmergencyServiceStatus.ARRIVED, concernedVehicles)
        Mockito.verify(sender).sendMessage(expectedNotification, "notifications.vehicle")
    }

    @Test
    fun testNotifyVehiclesOfAccidentUpdateShouldInvokeSenderWithCorrectNotification_Site_Clearance() {
        val onGoingAccident = TestDataProvider.testLiveAccident(System.currentTimeMillis())
        val concernedVehicles = ConcernedVehicles(listOf("1", "2", "3"), listOf("4", "5", "6"))
        val time = System.currentTimeMillis()
        val updatedAccident = onGoingAccident.withSiteClearing(time)
        val expectedNotification = VehicleNotification(
            concernedVehicles.concernedFarAwayVehicles.toTypedArray(),
            concernedVehicles.concernedNearByVehicles.toTypedArray(),
            "someOngoingAccident",
            time,
            GpsLocation(updatedAccident.location.y, updatedAccident.location.x),
            EmergencyServiceStatus.AREA_CLEARED,
            true,
            10.0
        )
        notifier.notifyVehiclesOfAccidentUpdate(updatedAccident, EmergencyServiceStatus.AREA_CLEARED, concernedVehicles)
        Mockito.verify(sender).sendMessage(expectedNotification, "notifications.vehicle")
    }
}