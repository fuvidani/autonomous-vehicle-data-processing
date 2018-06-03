package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.NotificationServiceTestApplication
import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.EmergencyServiceNotificationRepository
import at.ac.tuwien.dse.ss18.group05.service.EmergencyServiceNotificationService
import at.ac.tuwien.dse.ss18.group05.service.IEmergencyServiceNotificationService
import com.google.gson.Gson
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
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration

@RunWith(SpringRunner::class)
@SpringBootTest(value = ["application.yml"], classes = [NotificationServiceTestApplication::class])
class EmergencyServiceNotificationControllerTest {

    private val pingNotification = EmergencyServiceNotification(id = "", accidentId = "", timeStamp = 0L, location = GpsLocation(0.0, 0.0), model = "", passengers = 0, status = EmergencyServiceStatus.UNKNOWN)

    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin

    @Rule
    @JvmField
    final val restDocumentation = JUnitRestDocumentation()

    @Autowired
    private lateinit var repository: EmergencyServiceNotificationRepository

    @Autowired
    private lateinit var receiver: Receiver<EmergencyServiceNotification>

    @Autowired
    private lateinit var controller: EmergencyServiceNotificationController

    @Autowired
    private lateinit var template: MongoTemplate

    private val gson = Gson()
    private val generator = TestDataGenerator()
    private lateinit var client: WebTestClient
    private lateinit var service: IEmergencyServiceNotificationService

    @Before
    fun setUp() {
        service = EmergencyServiceNotificationService(receiver, repository)
        client = WebTestClient.bindToController(controller)
                .configureClient()
                .baseUrl("http://notification-service.com/notifications/ems")
                .build()

        template.dropCollection(EmergencyServiceNotification::class.java)
        val notifications = generator.getAllEMSNotifications()
        notifications.forEach {
            template.insert(it)
        }
    }

    @Test
    fun getNotifications_gettingFluxStreamForEMS_shouldReturnFluxAndNotifications() {
        val notifications = generator.getAllEMSNotifications()

        val stream = client.get()
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult(EmergencyServiceNotification::class.java)

        StepVerifier.create(stream.responseBody)
                .expectSubscription()
                .then {
                    Flux.fromArray(notifications)
                            .delayElements(Duration.ofSeconds(2))
                            .subscribe { receiver.receiveMessage(gson.toJson(it)) }
                }
                .expectNext(pingNotification)
                .expectNextCount(2)
                .thenCancel()
                .verify()
    }

    @Test
    fun historyNotifications_requestingHistoryNotifications_shouldReturnDatabaseNotifications() {
        val stream = client.get().uri("/findAllHistoryNotifications")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult(EmergencyServiceNotification::class.java)

        StepVerifier.create(stream.responseBody)
                .expectSubscription()
                .expectNext(generator.getFirstEMSNotification())
                .expectNext(generator.getSecondEMSNotification())
                .thenCancel()
                .verify()
    }
}