package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.NotificationServiceTestApplication
import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.messaging.ManufacturerNotificationReceiver
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
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
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
class ManufacturerNotificationControllerDocumentationTest {

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
    private lateinit var template: MongoTemplate

    private val gson = Gson()
    private val generator = TestDataGenerator()
    private lateinit var client: WebTestClient
    private lateinit var service: IManufacturerNotificationService
    private lateinit var processor: ReplayProcessor<ManufacturerNotification>
    private lateinit var receiver: Receiver<ManufacturerNotification>
    private lateinit var controller: ManufacturerNotificationController

    @Before
    fun setUp() {

        processor = ReplayProcessor.create<ManufacturerNotification>(2)

        receiver = ManufacturerNotificationReceiver(repository, processor, gson)
        service = ManufacturerNotificationService(receiver, repository)
        controller = ManufacturerNotificationController(service)
        client = WebTestClient.bindToController(controller)
                .configureClient()
                .baseUrl("http://notification-service.com/notifications/manufacturer")
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
                .responseTimeout(Duration.ofSeconds(15))
                .build()

        template.dropCollection(ManufacturerNotification::class.java)
        val notifications = generator.getAllManufacturerNotifications()
        notifications.forEach {
            template.insert(it)
        }
    }

    @Test
    fun historyNotifications_requestingHistoryNotifications_shouldDocument() {
        val notifications = generator.getAllManufacturerNotifications()
        val manufacturerId = notifications[0].manufacturerId

        client.get().uri("/{id}/findAllHistoryNotifications", manufacturerId)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(
                        document(
                                "manufacturer-a-history-notifications",
                                pathParameters(
                                        parameterWithName("id")
                                                .description("the id of the manufacturer")
                                ),
                                responseFields(
                                        fieldWithPath("id")
                                                .type(JsonFieldType.STRING)
                                                .description("the id of the notification"),
                                        fieldWithPath("timeStamp")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the utc timestamp as number for the notificatio"),
                                        fieldWithPath("vehicleIdentificationNumber")
                                                .type(JsonFieldType.STRING)
                                                .description("the id of the vehicle which is concerned by this notification"),
                                        fieldWithPath("manufacturerId")
                                                .type(JsonFieldType.STRING)
                                                .description("the id of the manufacturer itself"),
                                        fieldWithPath("model")
                                                .type(JsonFieldType.STRING)
                                                .description("the model of the vehicle"),
                                        fieldWithPath("location.lat")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the latitude of the event"),
                                        fieldWithPath("location.lon")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the longitude of the event"),
                                        fieldWithPath("eventInfo")
                                                .type(JsonFieldType.STRING)
                                                .description("the event information - none - near crash or crash event"),
                                        fieldWithPath("accidentId")
                                                .type(JsonFieldType.STRING)
                                                .description("the accident id that occurred")
                                )

                        )
                )
    }

    @Test
    fun manufacturerNotification_requestingStreamOfManufacturerNotifications_shouldDocument() {

        val notifications = generator.getAllManufacturerNotifications()
        val manufacturerId = notifications[0].manufacturerId

        Flux.fromArray(notifications)
                .delaySubscription(Duration.ofSeconds(1))
                .delayElements(Duration.ofSeconds(1))
                .doOnNext { receiver.receiveMessage(gson.toJson(it)) }
                .doOnComplete { Mono.delay(Duration.ofSeconds(1)).subscribe { processor.onComplete() } }
                .subscribe()

        client.get().uri("/{id}", manufacturerId)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(
                        document(
                                "manufacturer-a-stream-notifications",
                                pathParameters(
                                        parameterWithName("id")
                                                .description("the id of the manufacturer")
                                ),
                                responseFields(
                                        fieldWithPath("id")
                                                .type(JsonFieldType.STRING)
                                                .description("the id of the notification"),
                                        fieldWithPath("timeStamp")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the utc timestamp as number for the notificatio"),
                                        fieldWithPath("vehicleIdentificationNumber")
                                                .type(JsonFieldType.STRING)
                                                .description("the id of the vehicle which is concerned by this notification"),
                                        fieldWithPath("manufacturerId")
                                                .type(JsonFieldType.STRING)
                                                .description("the id of the manufacturer itself"),
                                        fieldWithPath("model")
                                                .type(JsonFieldType.STRING)
                                                .description("the model of the vehicle"),
                                        fieldWithPath("location.lat")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the latitude of the event"),
                                        fieldWithPath("location.lon")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the longitude of the event"),
                                        fieldWithPath("eventInfo")
                                                .type(JsonFieldType.STRING)
                                                .description("the event information - none - near crash or crash event"),
                                        fieldWithPath("accidentId")
                                                .type(JsonFieldType.STRING)
                                                .description("the accident id that occurred")
                                )
                        )
                )
    }
}