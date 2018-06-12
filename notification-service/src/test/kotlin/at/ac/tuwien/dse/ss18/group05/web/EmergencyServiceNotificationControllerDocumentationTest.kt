package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.NotificationServiceTestApplication
import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.messaging.EmsNotificationReceiver
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
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.ReplayProcessor
import java.time.Duration

@RunWith(SpringRunner::class)
@SpringBootTest(value = ["application.yml"], classes = [NotificationServiceTestApplication::class])
class EmergencyServiceNotificationControllerDocumentationTest {

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
    private lateinit var template: MongoTemplate

    private val gson = Gson()
    private val generator = TestDataGenerator()
    private lateinit var client: WebTestClient
    private lateinit var service: IEmergencyServiceNotificationService
    private lateinit var receiver: Receiver<EmergencyServiceNotification>
    private lateinit var controller: EmergencyServiceNotificationController
    private lateinit var processor: ReplayProcessor<EmergencyServiceNotification>

    @Before
    fun setUp() {

        processor = ReplayProcessor.create<EmergencyServiceNotification>(2)

        receiver = EmsNotificationReceiver(repository, processor, gson)
        service = EmergencyServiceNotificationService(receiver, repository)
        controller = EmergencyServiceNotificationController(service)
        client = WebTestClient.bindToController(controller)
                .configureClient()
                .baseUrl("http://notification-service.com/notifications/ems")
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
                .responseTimeout(Duration.ofSeconds(15))
                .build()

        template.dropCollection(EmergencyServiceNotification::class.java)
        val notifications = generator.getAllEMSNotifications()
        notifications.forEach {
            template.insert(it)
        }
    }

    @Test
    fun historyNotifications_requestingHistoryNotifications_shouldDocument() {
        client.get().uri("/findAllHistoryNotifications")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(
                        document(
                                "ems-history-notifications",
                                responseFields(
                                        fieldWithPath("id")
                                                .type(JsonFieldType.STRING)
                                                .description("the id of the notification"),
                                        fieldWithPath("accidentId")
                                                .type(JsonFieldType.STRING)
                                                .description("the accident id that occurred"),
                                        fieldWithPath("timeStamp")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the utc timestamp when the event occurred"),
                                        fieldWithPath("location.lat")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the latitude of the event"),
                                        fieldWithPath("location.lon")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the longitude of the event"),
                                        fieldWithPath("model")
                                                .type(JsonFieldType.STRING)
                                                .description("the type of the model - for first responders of interest (electric vehicles)"),
                                        fieldWithPath("passengers")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the number of passengers - for first responders of interest"),
                                        fieldWithPath("status")
                                                .type(JsonFieldType.STRING)
                                                .description("the status of the notification")
                                )
                        )
                )
    }

    @Test
    fun emsNotifications_requestingStreamOfNotifications_shouldDocument() {
        val notifications = generator.getAllEMSNotifications()

        Flux.fromArray(notifications)
                .delaySubscription(Duration.ofSeconds(1))
                .delayElements(Duration.ofSeconds(1))
                .doOnNext { receiver.receiveMessage(gson.toJson(it)) }
                .doOnComplete { Mono.delay(Duration.ofSeconds(1)).subscribe { processor.onComplete() } }
                .subscribe()

        client.get()
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(
                        document(
                                "ems-stream-notifications",
                                responseFields(
                                        fieldWithPath("id")
                                                .type(JsonFieldType.STRING)
                                                .description("the id of the notification"),
                                        fieldWithPath("accidentId")
                                                .type(JsonFieldType.STRING)
                                                .description("the accident id that occurred"),
                                        fieldWithPath("timeStamp")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the utc timestamp when the event occurred"),
                                        fieldWithPath("location.lat")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the latitude of the event"),
                                        fieldWithPath("location.lon")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the longitude of the event"),
                                        fieldWithPath("model")
                                                .type(JsonFieldType.STRING)
                                                .description("the type of the model - for first responders of interest (electric vehicles)"),
                                        fieldWithPath("passengers")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the number of passengers - for first responders of interest"),
                                        fieldWithPath("status")
                                                .type(JsonFieldType.STRING)
                                                .description("the status of the notification")
                                )
                        )
                )
    }
}