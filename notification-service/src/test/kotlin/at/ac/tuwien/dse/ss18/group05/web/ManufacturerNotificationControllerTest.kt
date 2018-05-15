package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.NotificationServiceTestApplication
import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.ManufacturerNotificationRepository
import at.ac.tuwien.dse.ss18.group05.service.IManufacturerNotificationService
import at.ac.tuwien.dse.ss18.group05.service.ManufacturerNotificationService
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
class ManufacturerNotificationControllerTest {

    private val pingNotification = ManufacturerNotification(id = "", timeStamp = 0L, accidentId = "", vehicleIdentificationNumber = "", manufacturerId = "", model = "", location = GpsLocation(0.0, 0.0), eventInfo = EventInformation.NONE)

    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin

    @Rule
    @JvmField
    final val restDocumentation = JUnitRestDocumentation()

    @Autowired
    private lateinit var repository: ManufacturerNotificationRepository

    @Autowired
    private lateinit var receiver: Receiver<ManufacturerNotification>

    @Autowired
    private lateinit var controller: ManufacturerNotificationController

    @Autowired
    private lateinit var template: MongoTemplate

    private val gson = Gson()
    private val generator = TestDataGenerator()
    private lateinit var client: WebTestClient
    private lateinit var service: IManufacturerNotificationService

    @Before
    fun setUp() {
        service = ManufacturerNotificationService(receiver, repository)
        client = WebTestClient.bindToController(controller)
                .configureClient()
                .baseUrl("http://notification-service.com/notifications/manufacturer")
                .build()

        template.dropCollection(ManufacturerNotification::class.java)
        val notifications = generator.getAllManufacturerNotifications()
        notifications.forEach {
            template.insert(it)
        }
    }

    @Test
    fun getNotifications_gettingFluxStreamForEMS_shouldReturnFluxAndNotifications() {
        val notifications = generator.getAllManufacturerNotifications()
        val manufacturerId = notifications[0].manufacturerId

        val stream = client.get().uri("/{id}", manufacturerId)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult(ManufacturerNotification::class.java)

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
        val notifications = generator.getAllManufacturerNotifications()
        val manufacturerId = notifications[0].manufacturerId

        val stream = client.get().uri("/{id}/findAllHistoryNotifications", manufacturerId)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult(ManufacturerNotification::class.java)

        StepVerifier.create(stream.responseBody)
                .expectSubscription()
                .expectNext(generator.getManufacturerAFirstNotification())
                .expectNext(generator.getManufacturerASecondNotification())
                .thenCancel()
                .verify()
    }
}