package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.TrackerServiceApplication
import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.messaging.VehicleDataRecordReceiver
import at.ac.tuwien.dse.ss18.group05.repository.VehicleDataRecordRepository
import at.ac.tuwien.dse.ss18.group05.service.ITrackerService
import at.ac.tuwien.dse.ss18.group05.service.TrackerService
import at.ac.tuwien.dse.ss18.group05.service.client.VehicleServiceClient
import com.google.gson.Gson
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.TopicProcessor
import reactor.util.concurrent.Queues
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

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
@SpringBootTest(value = ["application.yml"], classes = [TrackerServiceApplication::class])
class TrackerServiceDocumentationTests {

    @Rule
    @JvmField
    final val restDocumentation = JUnitRestDocumentation()
    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin
    @Autowired
    private lateinit var repository: VehicleDataRecordRepository
    @MockBean
    private lateinit var vehicleServiceClient: VehicleServiceClient
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    private lateinit var trackerServiceController: TrackerServiceController
    private lateinit var trackerService: ITrackerService
    private lateinit var receiver: Receiver
    private lateinit var processor: TopicProcessor<VehicleDataRecord>
    private lateinit var client: WebTestClient
    private lateinit var audiDataRecords: MutableList<VehicleDataRecord>
    private val gson = Gson()

    @Before
    fun setUp() {
        val vehicles = listOf(
            TestDataProvider.testVehicleAcura(),
            TestDataProvider.testVehicleAudi(),
            TestDataProvider.testVehicleTesla()
        )
        Mockito.`when`(vehicleServiceClient.getAllVehicles()).thenReturn(vehicles)
        processor = TopicProcessor.builder<VehicleDataRecord>()
            .autoCancel(false)
            .share(true)
            .name("something")
            .bufferSize(Queues.SMALL_BUFFER_SIZE)
            .build()
        receiver = VehicleDataRecordReceiver(gson, repository, processor)
        trackerService = TrackerService(receiver, vehicleServiceClient, repository)
        trackerServiceController = TrackerServiceController(trackerService)
        client = WebTestClient.bindToController(trackerServiceController)
            .configureClient()
            .baseUrl("http://tracker-service.com/tracking")
            .filter(documentationConfiguration(restDocumentation))
            .responseTimeout(Duration.ofSeconds(15))
            .build()
        mongoTemplate.dropCollection(VehicleDataRecord::class.java)
        audiDataRecords = emptyList<VehicleDataRecord>().toMutableList()
    }

    @Test
    fun getVehiclesTrackingHistory() {
        insertVehicleDataRecordsForAudi()
        client.get().uri("/history/manufacturer/{manufacturerId}", "Audi")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "tracking-history-audi",
                    pathParameters(
                        parameterWithName("manufacturerId")
                            .description("The ID of the manufacturer")
                    ),
                    responseFields(
                        fieldWithPath("id")
                            .type(JsonFieldType.STRING)
                            .description("The data record's ID"),
                        fieldWithPath("timestamp")
                            .type(JsonFieldType.NUMBER)
                            .description("Time of recording in milliseconds (UTC)"),
                        fieldWithPath("vehicleIdentificationNumber")
                            .type(JsonFieldType.STRING)
                            .description("The vehicle's world-wide unique Vehicle Identification Number"),
                        fieldWithPath("model")
                            .type(JsonFieldType.STRING)
                            .description("The vehicle's type of model"),
                        fieldWithPath("location.lat")
                            .type(JsonFieldType.NUMBER)
                            .description("The latitude coordinate of the vehicle at the moment of recording"),
                        fieldWithPath("location.lon")
                            .type(JsonFieldType.NUMBER)
                            .description("The longitude coordinate of the vehicle at the moment of recording")
                    )
                )
            )
    }

    @Test
    fun getVehiclesTrackingStream() {
        val dataRecords = listOf(
            TestDataProvider.testVehicleDataRecordTesla(),
            TestDataProvider.testVehicleDataRecordTesla(),
            TestDataProvider.testVehicleDataRecordTesla()
        )
        Flux.fromIterable(dataRecords)
            .delaySubscription(Duration.ofSeconds(2))
            .delayElements(Duration.ofSeconds(2))
            .doOnNext { receiver.receiveMessage(gson.toJson(it)) }
            .doOnComplete { Mono.delay(Duration.ofSeconds(2)).subscribe { processor.onComplete() } }
            .subscribe()
        client.get().uri("/manufacturer/{manufacturerId}", "Tesla")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "tracking-stream-tesla",
                    pathParameters(
                        parameterWithName("manufacturerId")
                            .description("The ID of the manufacturer")
                    ),
                    responseFields(
                        fieldWithPath("id")
                            .type(JsonFieldType.STRING)
                            .description("The data record's ID"),
                        fieldWithPath("timestamp")
                            .type(JsonFieldType.NUMBER)
                            .description("Time of recording in milliseconds (UTC)"),
                        fieldWithPath("vehicleIdentificationNumber")
                            .type(JsonFieldType.STRING)
                            .description("The vehicle's world-wide unique Vehicle Identification Number"),
                        fieldWithPath("model")
                            .type(JsonFieldType.STRING)
                            .description("The vehicle's type of model"),
                        fieldWithPath("location.lat")
                            .type(JsonFieldType.NUMBER)
                            .description("The latitude coordinate of the vehicle at the moment of recording"),
                        fieldWithPath("location.lon")
                            .type(JsonFieldType.NUMBER)
                            .description("The longitude coordinate of the vehicle at the moment of recording")
                    )
                )
            )
    }

    private fun insertVehicleDataRecordsForAudi() {
        audiDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordAudi()).block()!!)
        audiDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordAudi()).block()!!)
        audiDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordAudi()).block()!!)
    }
}