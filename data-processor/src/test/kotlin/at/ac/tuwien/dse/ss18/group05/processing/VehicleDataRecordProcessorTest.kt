package at.ac.tuwien.dse.ss18.group05.processing

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import at.ac.tuwien.dse.ss18.group05.messaging.notification.INotifier
import at.ac.tuwien.dse.ss18.group05.repository.LiveAccidentRepository
import at.ac.tuwien.dse.ss18.group05.service.IVehicleLocationService
import at.ac.tuwien.dse.ss18.group05.service.client.VehicleServiceClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono

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
class VehicleDataRecordProcessorTest {

    private lateinit var processor: DataProcessor<VehicleDataRecord>
    @MockBean
    private lateinit var vehicleLocationService: IVehicleLocationService
    @MockBean
    private lateinit var notifier: INotifier
    @MockBean
    private lateinit var accidentRepository: LiveAccidentRepository
    @MockBean
    private lateinit var vehicleServiceClient: VehicleServiceClient

    @Before
    fun setUp() {
        Mockito.`when`(vehicleServiceClient.getAllVehicles()).thenReturn(
            listOf(
                TestDataProvider.testVehicleTesla(),
                TestDataProvider.testVehicleAudi(),
                TestDataProvider.testVehicleAcura()
            )
        )
        Mockito.`when`(vehicleLocationService.save(any(VehicleLocation::class.java)))
            .thenReturn(Mono.just(TestDataProvider.getTestVehicleLocation0()))
        processor =
                VehicleDataRecordProcessor(vehicleLocationService, accidentRepository, notifier, vehicleServiceClient)
    }

    @Test
    fun testProcessSimpleEventDataShouldSimplyUpdateLocation() {
        val dataRecord = TestDataProvider.testVehicleDataRecordAudi()
        processor.process(dataRecord)
        Mockito.verify(vehicleLocationService, times(1)).save(dataRecordToVehicleLocation(dataRecord))
        // verify that other methods otherwise used in crashes or near crashes are not invoked
        Mockito.verify(vehicleLocationService, Mockito.never()).findVehiclesInRadius(any(GpsLocation::class.java))
        Mockito.verify(vehicleLocationService, Mockito.never()).findVehiclesInRadius(any(GeoJsonPoint::class.java))
        Mockito.verifyZeroInteractions(notifier)
        Mockito.verifyZeroInteractions(vehicleServiceClient)
    }

    @Test
    fun testProcessNearCrashEventShouldNotifyManufacturer() {
        val dataRecord = TestDataProvider.testVehicleDataRecordNearCrashTesla()
        processor.process(dataRecord)
        Mockito.verify(vehicleLocationService, times(1)).save(dataRecordToVehicleLocation(dataRecord))
        Mockito.verify(notifier, times(1)).notifyManufacturer(dataRecord, null, "Tesla")
        Mockito.verify(vehicleServiceClient, times(1)).getAllVehicles()
        Mockito.verifyZeroInteractions(vehicleLocationService)
    }

    @Test
    fun testProcessNearCrashEventWithUnknownManufacturerShouldNotifyManufacturer() {
        Mockito.`when`(vehicleServiceClient.getAllVehicles())
            .thenReturn(listOf(TestDataProvider.testVehicleAudi(), TestDataProvider.testVehicleAcura()))
        val dataRecord = TestDataProvider.testVehicleDataRecordNearCrashTesla()
        processor.process(dataRecord)
        Mockito.verify(vehicleLocationService, times(1)).save(dataRecordToVehicleLocation(dataRecord))
        Mockito.verify(notifier, times(1)).notifyManufacturer(dataRecord, null, "")
        Mockito.verify(vehicleServiceClient, times(1)).getAllVehicles()
        Mockito.verifyZeroInteractions(vehicleLocationService)
    }

    @Test
    fun testProcessCrashEventShouldNotifyVehiclesEmergencyServiceAndManufacturer() {
        val dataRecord = TestDataProvider.testVehicleDataRecordCrashTesla()
        Mockito.`when`(vehicleLocationService.findVehiclesInRadius(any(GpsLocation::class.java))).thenReturn(
            Mono.just(Pair(listOf("1", "2", "3"), listOf("4", "5", "6")))
        )
        val accident = dataRecord.toDefaultLiveAccident()
        val expectedSavedAccident = LiveAccident(
            "someGeneratedId",
            accident.vehicleMetaData,
            accident.location,
            accident.passengers,
            accident.timestampOfAccident,
            accident.timestampOfServiceArrival,
            accident.timestampOfSiteClearing
        )
        Mockito.`when`(accidentRepository.save(accident)).thenReturn(Mono.just(expectedSavedAccident))
        processor.process(dataRecord)
        Mockito.verify(vehicleLocationService, times(1)).save(dataRecordToVehicleLocation(dataRecord))
        Mockito.verify(accidentRepository, times(1)).save(accident)
        Mockito.verify(vehicleLocationService, times(1)).findVehiclesInRadius(dataRecord.sensorInformation.location)
        Mockito.verify(vehicleLocationService, Mockito.never()).findVehiclesInRadius(any(GeoJsonPoint::class.java))
        Mockito.verify(vehicleServiceClient, times(1)).getAllVehicles()
        Mockito.verify(notifier, times(1)).notifyManufacturer(dataRecord, "someGeneratedId", "Tesla")
        Mockito.verify(notifier, times(1)).notifyEmergencyService(dataRecord, "someGeneratedId")
        Mockito.verify(notifier, times(1)).notifyVehiclesOfNewAccident(
            dataRecord, "someGeneratedId",
            ConcernedVehicles(listOf("4", "5", "6"), listOf("1", "2", "3"))
        )
        Mockito.verifyZeroInteractions(notifier)
        Mockito.verifyZeroInteractions(vehicleLocationService)
        Mockito.verifyZeroInteractions(accidentRepository)
    }

    private fun dataRecordToVehicleLocation(dataRecord: VehicleDataRecord): VehicleLocation {
        return VehicleLocation(
            dataRecord.metaData.identificationNumber,
            GeoJsonPoint(dataRecord.sensorInformation.location.lon, dataRecord.sensorInformation.location.lat)
        )
    }

    // Kotlin<->Java Mockito type inference workaround
    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}