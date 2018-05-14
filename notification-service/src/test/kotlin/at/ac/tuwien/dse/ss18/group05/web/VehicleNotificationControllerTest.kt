package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.NotificationServiceTestApplication
import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.repository.VehicleNotificationRepository
import at.ac.tuwien.dse.ss18.group05.service.IVehicleNotificationService
import at.ac.tuwien.dse.ss18.group05.service.VehicleNotificationService
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.TopicProcessor
import reactor.test.StepVerifier
import java.time.Duration
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(value = ["application.yml"], classes = [NotificationServiceTestApplication::class])
class VehicleNotificationControllerTest {

    private val pingNotification = VehicleNotification(id = "", vehicleIdentificationNumber = "", accidentId = "", timestamp = 0L, location = GpsLocation(0.0, 0.0), emergencyServiceStatus = EmergencyServiceStatus.UNKNOWN, specialWarning = null, targetSpeed = null)

    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin


    @Rule
    @JvmField
    final val restDocumentation = JUnitRestDocumentation()

    @Autowired
    private lateinit var repository: VehicleNotificationRepository

    @Autowired
    private lateinit var vehicleNotificationController: VehicleNotificationController

    @Autowired
    private lateinit var template: MongoTemplate

    private val generator = TestDataGenerator()
    private lateinit var client: WebTestClient
    private lateinit var service: IVehicleNotificationService


    @Before
    fun setUp() {

        service = VehicleNotificationService(repository)
        client = WebTestClient.bindToController(vehicleNotificationController)
                .configureClient()
                .baseUrl("http://notification-service.com/notifications")
                .build()

        template.dropCollection(VehicleNotification::class.java)
        val notifications = generator.getAllVehicleNotifications()
        notifications.forEach {
            template.insert(it)
        }

    }

    @Test
    fun getNotifications_gettingFluxStreamForVehicle_shouldReturnFluxAndNotifications() {
        val notifications = generator.getAllVehicleNotifications()

        val vehicleId = notifications[0].vehicleIdentificationNumber


        val stream = client.get().uri("/vehicle/{vehicleId}", vehicleId)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult(VehicleNotification::class.java)

        val incomingVehicleNotification = IncomingVehicleNotification(
                concernedNearByVehicles = arrayOf(vehicleId), concernedFarAwayVehicles = arrayOf(vehicleId), accidentId = "acc_id", timestamp = 1L, location = GpsLocation(0.0, 0.0), emergencyServiceStatus = EmergencyServiceStatus.UNKNOWN, specialWarning = true, targetSpeed = 30.0
        )

        StepVerifier.create(stream.responseBody)
                .expectSubscription()
                .then {
                    Flux.fromIterable(listOf(incomingVehicleNotification))
                            .delaySubscription(Duration.ofSeconds(2))
                            .delayElements(Duration.ofSeconds(3))
                            .doOnNext {  service.handleIncomingVehicleNotification(it)}
                            .doOnComplete { service.closeStream() }
                            .subscribe()
                            //.subscribe { service.handleIncomingVehicleNotification(it) }
                }
                .expectNext(pingNotification)
                .expectNextCount(1)
                .expectNoEvent(Duration.ofSeconds(2))
                .expectComplete()
                .verify()

    }

}