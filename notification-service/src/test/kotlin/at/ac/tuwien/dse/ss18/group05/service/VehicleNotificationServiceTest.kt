package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.NotificationServiceTestApplication
import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.repository.VehicleNotificationRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
@SpringBootTest(value = ["application.yml"], classes = [NotificationServiceTestApplication::class])
class VehicleNotificationServiceTest {

    @MockBean
    private lateinit var admin: RabbitAdmin

    @Autowired
    private lateinit var repository: VehicleNotificationRepository

    private lateinit var service: IVehicleNotificationService

    @Autowired
    private lateinit var template: MongoTemplate

    private val generator = TestDataGenerator()

    @Before
    fun setUp() {
        service = VehicleNotificationService(repository)
        template.dropCollection(VehicleNotification::class.java)
        val notifications = generator.getAllVehicleNotifications()
        notifications.forEach {
            template.insert(it)
        }
    }

    @Test
    fun findAll_findingAllStoredNotifications_shouldReturnInInsertionOrder() {
        val vehicleA = "first_vehicle_id"
        val vehicleB = "second_vehicle_id"

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
    fun streamNotifications_streamingWithEmptyNotifications_shouldReturnEmpty() {
        val nearByVehicle = "random_id"
        val farAwayVehicle = "far_away_id"
        val nearFlux = service.getNotificationForVehicle(nearByVehicle)
        val farFlux = service.getNotificationForVehicle(farAwayVehicle)

        val incomingVehicleNotification = IncomingVehicleNotification(
                concernedNearByVehicles = arrayOf(nearByVehicle), concernedFarAwayVehicles = arrayOf(farAwayVehicle), accidentId = "acc_id", timestamp = 1L, location = GpsLocation(0.0, 0.0), emergencyServiceStatus = EmergencyServiceStatus.UNKNOWN, specialWarning = true, targetSpeed = 30.0
        )
        val nearByNotification = VehicleNotification(nearByVehicle, incomingVehicleNotification)


        StepVerifier
                .create(nearFlux)
                .expectSubscription()
                .then {
                    service.handleIncomingVehicleNotification(incomingVehicleNotification)
                    service.closeStream()
                }
                .expectNext(nearByNotification)
                .verifyComplete()


        incomingVehicleNotification.targetSpeed = null
        incomingVehicleNotification.specialWarning = null
        val farNotification = VehicleNotification(farAwayVehicle, incomingVehicleNotification)

        StepVerifier
                .create(farFlux)
                .expectSubscription()
                .then {
                    service.handleIncomingVehicleNotification(incomingVehicleNotification)
                    service.closeStream()
                }
                .expectNext(farNotification)
                .verifyComplete()

    }

}