package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.NotificationServiceTestApplication
import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.messaging.VehicleNotificationReceiver
import at.ac.tuwien.dse.ss18.group05.repository.VehicleNotificationRepository
import at.ac.tuwien.dse.ss18.group05.service.IVehicleNotificationService
import at.ac.tuwien.dse.ss18.group05.service.VehicleNotificationService
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
class VehicleNotificationControllerDocumentationTest {

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
    private lateinit var template: MongoTemplate

    private val gson = Gson()
    private val generator = TestDataGenerator()
    private lateinit var client: WebTestClient
    private lateinit var service: IVehicleNotificationService
    private lateinit var processor: ReplayProcessor<VehicleNotification>
    private lateinit var receiver: Receiver<VehicleNotification>
    private lateinit var vehicleNotificationController: VehicleNotificationController

    @Before
    fun setUp() {

        processor = ReplayProcessor.create<VehicleNotification>(2)

        receiver = VehicleNotificationReceiver(repository, processor, gson)
        service = VehicleNotificationService(receiver, repository)
        vehicleNotificationController = VehicleNotificationController(service)

        client = WebTestClient.bindToController(vehicleNotificationController)
                .configureClient()
                .baseUrl("http://notification-service.com/notifications")
                .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
                .responseTimeout(Duration.ofSeconds(15))
                .build()

        template.dropCollection(VehicleNotification::class.java)
        val notifications = generator.getAllVehicleNotifications()
        notifications.forEach {
            template.insert(it)
        }
    }

    @Test
    fun vehicleNotifications_requestingStreamOfVehicleNotifications_shouldDocument() {
        val vehicleId = generator.getAllVehicleNotifications()[0].vehicleIdentificationNumber

        val incomingVehicleNotification = IncomingVehicleNotification(
                concernedNearByVehicles = arrayOf(vehicleId), concernedFarAwayVehicles = emptyArray(), accidentId = "acc_id", timestamp = 1L, location = GpsLocation(0.0, 0.0), emergencyServiceStatus = EmergencyServiceStatus.UNKNOWN, specialWarning = true, targetSpeed = 30.0
        )

        Flux.fromIterable(listOf(incomingVehicleNotification))
                .delaySubscription(Duration.ofSeconds(1))
                .delayElements(Duration.ofSeconds(1))
                .doOnNext { receiver.receiveMessage(gson.toJson(it)) }
                .doOnComplete { Mono.delay(Duration.ofSeconds(1)).subscribe { processor.onComplete() } }
                .subscribe()

        client.get().uri("/vehicle/{vehicleId}", vehicleId)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(
                        document(
                                "notifications-vehicle-stream",
                                pathParameters(
                                        parameterWithName("vehicleId")
                                                .description("the vehicle identification number")
                                ),
                                responseFields(
                                        fieldWithPath("id")
                                                .type(JsonFieldType.STRING)
                                                .description("the identification of the notification"),
                                        fieldWithPath("vehicleIdentificationNumber")
                                                .type(JsonFieldType.STRING)
                                                .description("the identification of the vehicle"),
                                        fieldWithPath("accidentId")
                                                .type(JsonFieldType.STRING)
                                                .description("the id of the accident that occurred"),
                                        fieldWithPath("timestamp")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the timestamp as a utc number"),
                                        fieldWithPath("location.lat")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the latitude of the accident location"),
                                        fieldWithPath("location.lon")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the longitude of the accident location"),
                                        fieldWithPath("emergencyServiceStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("the status of the accident - unknown - arrived or cleared"),
                                        fieldWithPath("specialWarning")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("boolean indicating if a special warning should be displayed from the car - can be null")
                                                .optional(),
                                        fieldWithPath("targetSpeed")
                                                .type(JsonFieldType.NUMBER)
                                                .description("the speed the car should adapt to - can be null")
                                                .optional()
                                )
                        )
                )
    }
}