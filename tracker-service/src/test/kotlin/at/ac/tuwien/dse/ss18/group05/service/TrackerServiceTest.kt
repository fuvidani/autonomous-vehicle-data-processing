package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.VehicleDataRecordRepository
import at.ac.tuwien.dse.ss18.group05.service.client.VehicleServiceClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
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
@RunWith(SpringRunner::class)
class TrackerServiceTest {

    private lateinit var trackerService: ITrackerService
    @MockBean
    private lateinit var repository: VehicleDataRecordRepository
    @MockBean
    private lateinit var vehicleServiceClient: VehicleServiceClient
    @MockBean
    private lateinit var receiver: Receiver

    @Before
    fun setUp() {
        val vehicles = listOf(
            TestDataProvider.testVehicleAcura(),
            TestDataProvider.testVehicleAudi(),
            TestDataProvider.testVehicleTesla()
        )
        val vehicleDataRecords = listOf(
            TestDataProvider.testVehicleDataRecordAcura(),
            TestDataProvider.testVehicleDataRecordAudi(),
            TestDataProvider.testVehicleDataRecordTesla()
        )
        Mockito.`when`(vehicleServiceClient.getAllVehicles()).thenReturn(vehicles)
        Mockito.`when`(repository.findAll()).thenReturn(Flux.fromIterable(vehicleDataRecords))
        Mockito.`when`(receiver.recordStream()).thenReturn(
            Flux.fromIterable(vehicleDataRecords + vehicleDataRecords)
                .delayElements(Duration.ofSeconds(1))
        )
        trackerService = TrackerService(receiver, vehicleServiceClient, repository)
    }

    @Test
    fun testGetTrackingHistoryShouldReturnCorrectDataRecords() {
        StepVerifier
            .create(trackerService.getTrackingHistoryForManufacturer("Acura"))
            .expectSubscription()
            .expectNext(TestDataProvider.testManufacturerDataRecordAcura())
            .expectComplete()
            .verify()
    }

    @Test
    fun testGetTrackingHistoryShouldReturnCorrectDataRecords2() {
        StepVerifier
            .create(trackerService.getTrackingHistoryForManufacturer("Audi"))
            .expectSubscription()
            .expectNext(TestDataProvider.testManufacturerDataRecordAudi())
            .expectComplete()
            .verify()
    }

    @Test
    fun testGetTrackingHistoryShouldReturnCorrectDataRecords3() {
        StepVerifier
            .create(trackerService.getTrackingHistoryForManufacturer("Tesla"))
            .expectSubscription()
            .expectNext(TestDataProvider.testManufacturerDataRecordTesla())
            .expectComplete()
            .verify()
    }

    @Test
    fun testGetTrackingHistoryWithInvalidManufacturerIdShouldReturnEmpty() {
        StepVerifier
            .create(trackerService.getTrackingHistoryForManufacturer("SomeInvalidManufacturerId"))
            .expectSubscription()
            .expectComplete()
            .verify()
    }

    @Test
    fun testGetTrackingStreamShouldStreamDownCorrectRecordsAcura() {
        StepVerifier
            .create(trackerService.getTrackingStreamForManufacturer("Acura"))
            .expectSubscription()
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAcura())
            .expectNoEvent(Duration.ofMillis(2800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAcura())
            .expectComplete()
            .verify()
    }

    @Test
    fun testGetTrackingStreamShouldStreamDownCorrectRecordsAudi() {
        StepVerifier
            .create(trackerService.getTrackingStreamForManufacturer("Audi"))
            .expectSubscription()
            .expectNoEvent(Duration.ofMillis(1800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAudi())
            .expectNoEvent(Duration.ofMillis(2800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAudi())
            .expectComplete()
            .verify()
    }

    @Test
    fun testGetTrackingStreamShouldStreamDownCorrectRecordsTesla() {
        StepVerifier
            .create(trackerService.getTrackingStreamForManufacturer("Tesla"))
            .expectSubscription()
            .expectNoEvent(Duration.ofMillis(2800))
            .expectNext(TestDataProvider.testManufacturerDataRecordTesla())
            .expectNoEvent(Duration.ofMillis(2800))
            .expectNext(TestDataProvider.testManufacturerDataRecordTesla())
            .expectComplete()
            .verify()
    }

    @Test
    fun testGetTrackingStreamWithInvalidManufacturerShouldNotEmitAnyData() {
        StepVerifier
            .create(trackerService.getTrackingStreamForManufacturer("SomeInvalidManufacturerId"))
            .expectSubscription()
            .expectNoEvent(Duration.ofMillis(5800))
            .expectComplete()
            .verify()
    }

    @Test
    fun testGetTrackingMergedValidStreams() {
        val acuraStream = trackerService.getTrackingStreamForManufacturer("Acura")
        val audiStream = trackerService.getTrackingStreamForManufacturer("Audi")
        val teslaStream = trackerService.getTrackingStreamForManufacturer("Tesla")

        val combined = acuraStream.mergeWith(audiStream).mergeWith(teslaStream)

        StepVerifier.create(combined)
            .expectSubscription()
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAcura())
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAudi())
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordTesla())
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAcura())
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAudi())
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordTesla())
            .expectComplete()
            .verify()
    }

    @Test
    fun testGetTrackingMergedMixedStreams() {
        val acuraStream = trackerService.getTrackingStreamForManufacturer("Acura")
        val audiStream = trackerService.getTrackingStreamForManufacturer("Audi")
        val teslaStream = trackerService.getTrackingStreamForManufacturer("Tesla")
        val invalidStream1 = trackerService.getTrackingStreamForManufacturer("SomeInvalidManufacturerId")
        val invalidStream2 = trackerService.getTrackingStreamForManufacturer("SomeInvalidManufacturerId2")

        val combined =
            acuraStream.mergeWith(audiStream).mergeWith(teslaStream).mergeWith(invalidStream1).mergeWith(invalidStream2)

        StepVerifier.create(combined)
            .expectSubscription()
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAcura())
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAudi())
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordTesla())
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAcura())
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordAudi())
            .expectNoEvent(Duration.ofMillis(800))
            .expectNext(TestDataProvider.testManufacturerDataRecordTesla())
            .expectComplete()
            .verify()
    }
}