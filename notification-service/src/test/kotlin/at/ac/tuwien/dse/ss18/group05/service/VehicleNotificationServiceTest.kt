package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.VehicleNotificationRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
class VehicleNotificationServiceTest {

    private val vehicleA = "first_vehicle_id"
    private val vehicleB = "second_vehicle_id"
    private val generator = TestDataGenerator()

    private lateinit var service: IVehicleNotificationService

    @MockBean
    private lateinit var receiver: Receiver<VehicleNotification>

    @MockBean
    private lateinit var repository: VehicleNotificationRepository

    @Before
    fun setUp() {
        val vehicleANotifications = generator.getAllVehicleNotifications().filter { n -> n.vehicleIdentificationNumber == vehicleA }
        val vehicleBNotifications = generator.getAllVehicleNotifications().filter { n -> n.vehicleIdentificationNumber == vehicleB }
        Mockito.`when`(repository.findByVehicleIdentificationNumber(vehicleA))
                .thenReturn(Flux.fromIterable(vehicleANotifications))
        Mockito.`when`(repository.findByVehicleIdentificationNumber(vehicleB))
                .thenReturn(Flux.fromIterable(vehicleBNotifications))

        Mockito.`when`(receiver.notificationStream()).thenReturn(Flux.fromArray(generator.getAllVehicleNotifications()))
        service = VehicleNotificationService(receiver, repository)
    }

    @Test
    fun findAll_findingAllStoredNotifications_shouldReturnInInsertionOrder() {
        StepVerifier
                .create(service.findHistoryNotificationsForVehicle(vehicleA))
                .expectNext(generator.getVehicleAFirstNotification())
                .expectNext(generator.getVehicleASecondNotification())
                .verifyComplete()

        StepVerifier
                .create(service.findHistoryNotificationsForVehicle(vehicleB))
                .expectNext(generator.getVehicleBFirstNotification())
                .expectNext(generator.getVehicleBSecondNotification())
                .verifyComplete()
    }

    @Test
    fun streamNotifications_streamingNotificationsForVehicles_shouldReturnExpectedNotifications() {
        val nearFlux = service.getStreamForVehicle(vehicleA)
        val farFlux = service.getStreamForVehicle(vehicleB)
        val combined = nearFlux.mergeWith(farFlux)
        StepVerifier
                .create(combined)
                .expectSubscription()
                .expectNext(generator.getVehicleAFirstNotification())
                .expectNext(generator.getVehicleASecondNotification())
                .expectNext(generator.getVehicleBFirstNotification())
                .expectNext(generator.getVehicleBSecondNotification())
                .expectComplete()
                .verify()
    }
}