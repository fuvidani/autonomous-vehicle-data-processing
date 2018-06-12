package at.ac.tuwien.dse.ss18.group05.processing

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import at.ac.tuwien.dse.ss18.group05.messaging.notification.INotifier
import at.ac.tuwien.dse.ss18.group05.repository.LiveAccidentRepository
import at.ac.tuwien.dse.ss18.group05.service.IVehicleLocationService
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
class EmergencyServiceMessageProcessorTest {

    private lateinit var processor: DataProcessor<EmergencyServiceMessage>
    @MockBean
    private lateinit var vehicleLocationService: IVehicleLocationService
    @MockBean
    private lateinit var notifier: INotifier
    @MockBean
    private lateinit var accidentRepository: LiveAccidentRepository

    @Before
    fun setUp() {
        processor = EmergencyServiceMessageProcessor(vehicleLocationService, accidentRepository, notifier)
    }

    @Test
    fun testProcessUnknownEmergencyServiceMessageShouldSimplyIgnore() {
        val message = EmergencyServiceMessage(0, "onGoingAccident", EmergencyServiceStatus.UNKNOWN)

        processor.process(message)

        Mockito.verifyZeroInteractions(vehicleLocationService)
        Mockito.verifyZeroInteractions(accidentRepository)
        Mockito.verifyZeroInteractions(notifier)
    }

    @Test
    fun testProcessServiceArrivalShouldNotifyVehicles() {
        val currentAccident = TestDataProvider.testLiveAccident(0)
        val message = EmergencyServiceMessage(123, "someOngoingAccident", EmergencyServiceStatus.ARRIVED)
        Mockito.`when`(accidentRepository.findById("someOngoingAccident")).thenReturn(Mono.just(currentAccident))
        Mockito.`when`(accidentRepository.save(currentAccident.withServiceArrival(123))).thenReturn(
            Mono.just(
                currentAccident.withServiceArrival(123)
            )
        )
        Mockito.`when`(vehicleLocationService.findVehiclesInRadius(any(GeoJsonPoint::class.java))).thenReturn(
            Mono.just(
                Pair(listOf("1", "2", "3"), listOf("4", "5", "6"))
            )
        )

        processor.process(message)

        Mockito.verify(accidentRepository, times(1)).findById("someOngoingAccident")
        Mockito.verify(accidentRepository, times(1)).save(currentAccident.withServiceArrival(123))
        Mockito.verifyZeroInteractions(accidentRepository)
        Mockito.verify(vehicleLocationService, times(1)).findVehiclesInRadius(currentAccident.location)
        Mockito.verifyZeroInteractions(vehicleLocationService)
        Mockito.verify(notifier, times(1)).notifyVehiclesOfAccidentUpdate(
            currentAccident.withServiceArrival(123),
            EmergencyServiceStatus.ARRIVED, ConcernedVehicles(listOf("4", "5", "6"), listOf("1", "2", "3"))
        )
        Mockito.verify(notifier, times(1)).notifyEMSWithUpdate(
            EmergencyServiceNotification(
                null, currentAccident.id!!, 1L, GpsLocation(0.0, 0.0), "", 0, EmergencyServiceStatus.ARRIVED
            )
        )
        Mockito.verifyZeroInteractions(notifier)
    }

    @Test
    fun testProcessSiteClearedShouldNotifyVehiclesAndStatisticsService() {
        val currentAccident = TestDataProvider.testLiveAccident(0).withServiceArrival(1)
        val message = EmergencyServiceMessage(2, "someOngoingAccident", EmergencyServiceStatus.AREA_CLEARED)
        val clearedAccident = currentAccident.withSiteClearing(2)
        Mockito.`when`(accidentRepository.findById("someOngoingAccident")).thenReturn(Mono.just(currentAccident))
        Mockito.`when`(accidentRepository.delete(any(LiveAccident::class.java))).thenReturn(Mono.empty())
        Mockito.`when`(vehicleLocationService.findVehiclesInRadius(any(GeoJsonPoint::class.java))).thenReturn(
            Mono.just(
                Pair(listOf("1", "2", "3"), listOf("4", "5", "6"))
            )
        )

        processor.process(message)

        Mockito.verify(accidentRepository, times(1)).findById("someOngoingAccident")
        Mockito.verify(accidentRepository, times(1)).delete(currentAccident)
        Mockito.verifyZeroInteractions(accidentRepository)
        Mockito.verify(vehicleLocationService, times(1)).findVehiclesInRadius(currentAccident.location)
        Mockito.verifyZeroInteractions(vehicleLocationService)
        Mockito.verify(notifier, times(1)).notifyVehiclesOfAccidentUpdate(
            clearedAccident,
            EmergencyServiceStatus.AREA_CLEARED, ConcernedVehicles(listOf("4", "5", "6"), listOf("1", "2", "3"))
        )
        Mockito.verify(notifier, times(1)).notifyStatisticsService(clearedAccident)
        Mockito.verify(notifier, times(1)).notifyEMSWithUpdate(
            EmergencyServiceNotification(
                null, currentAccident.id!!, 1L, GpsLocation(0.0, 0.0), "", 0, EmergencyServiceStatus.AREA_CLEARED
            )
        )
        Mockito.verifyZeroInteractions(notifier)
    }

    // Kotlin<->Java Mockito type inference workaround
    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}