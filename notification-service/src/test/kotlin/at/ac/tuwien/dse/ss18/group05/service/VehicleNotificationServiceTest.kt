package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.repository.VehicleNotificationRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration

@RunWith(SpringRunner::class)
class VehicleNotificationServiceTest {


    @MockBean
    private lateinit var repository: VehicleNotificationRepository

    private lateinit var service: IVehicleNotificationService


    private val generator = TestDataGenerator()

    @Before
    fun setUp() {

        service = VehicleNotificationService(repository)
    }

    @Test
    fun findAll_findingAllStoredNotifications_shouldReturnInInsertionOrder() {
        val vehicleA = "first_vehicle_id"
        val vehicleB = "second_vehicle_id"

        val vehicleANotifications = generator.getAllVehicleNotifications().filter { n -> n.vehicleIdentificationNumber == vehicleA }
        val vehicleBNotifications = generator.getAllVehicleNotifications().filter { n -> n.vehicleIdentificationNumber == vehicleB }

        Mockito.`when`(repository.findByvehicleIdentificationNumber(vehicleA))
                .thenReturn(Flux.fromIterable(vehicleANotifications))
        Mockito.`when`(repository.findByvehicleIdentificationNumber(vehicleB))
                .thenReturn(Flux.fromIterable(vehicleBNotifications))

        StepVerifier
                .create(service.findAllNotificationsForVehicle(vehicleA))
                .expectNext(generator.getVehicleAFirstNotification())
                .expectNext(generator.getVehicleASecondNotification())
                .verifyComplete()

        StepVerifier
                .create(service.findAllNotificationsForVehicle(vehicleB))
                .expectNext(generator.getVehicleBFirstNotification())
                .expectNext(generator.getVehicleBSecondNotification())
                .verifyComplete()
    }

    @Test
    fun streamNotifications_streamingNotificationsForVehicles_shouldReturnExpectedNotifications() {
        val nearByVehicle = "random_id"
        val farAwayVehicle = "far_away_id"
        val nearFlux = service.getNotificationForVehicle(nearByVehicle)
        val farFlux = service.getNotificationForVehicle(farAwayVehicle)

        val incomingVehicleNotification = IncomingVehicleNotification(
                concernedNearByVehicles = arrayOf(nearByVehicle), concernedFarAwayVehicles = arrayOf(farAwayVehicle), accidentId = "acc_id", timestamp = 1L, location = GpsLocation(0.0, 0.0), emergencyServiceStatus = EmergencyServiceStatus.UNKNOWN, specialWarning = true, targetSpeed = 30.0
        )
        val nearByNotification = VehicleNotification(vehicleId = nearByVehicle, incomingVehicleNotification = incomingVehicleNotification)
        Mockito.`when`(repository.save(nearByNotification)).thenReturn(Mono.just(nearByNotification))

        val farNotification = VehicleNotification(vehicleId = farAwayVehicle, incomingVehicleNotification = incomingVehicleNotification)
        farNotification.specialWarning = null
        farNotification.targetSpeed = null
        Mockito.`when`(repository.save(farNotification)).thenReturn(Mono.just(farNotification))

        val combined = nearFlux.mergeWith(farFlux)

        StepVerifier
                .create(combined)
                .expectSubscription()
                .expectNoEvent(Duration.ofMillis(900))
                .then {
                    service.handleIncomingVehicleNotification(incomingVehicleNotification)
                    service.closeStream()
                }
                .expectNext(nearByNotification)
                .expectNext(farNotification)
                .verifyComplete()

    }

}