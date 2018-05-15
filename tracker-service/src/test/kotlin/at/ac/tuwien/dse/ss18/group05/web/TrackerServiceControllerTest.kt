package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.TrackerServiceApplication
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerDataRecord
import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.dto.toManufacturerDataRecord
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.VehicleDataRecordRepository
import at.ac.tuwien.dse.ss18.group05.service.client.VehicleServiceClient
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
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
class TrackerServiceControllerTest {

    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin
    @Autowired
    private lateinit var repository: VehicleDataRecordRepository
    @MockBean
    private lateinit var vehicleServiceClient: VehicleServiceClient
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    @Autowired
    private lateinit var trackerServiceController: TrackerServiceController
    @Autowired
    private lateinit var receiver: Receiver
    private lateinit var client: WebTestClient
    private lateinit var acuraDataRecords: MutableList<VehicleDataRecord>
    private lateinit var audiDataRecords: MutableList<VehicleDataRecord>
    private lateinit var teslaDataRecords: MutableList<VehicleDataRecord>
    private val gson = Gson()
    private val pingManufacturerDataRecord = ManufacturerDataRecord("ping", 0L, "", "", GpsLocation(0.0, 0.0))

    @Before
    fun setUp() {
        val vehicles = listOf(
            TestDataProvider.testVehicleAcura(),
            TestDataProvider.testVehicleAudi(),
            TestDataProvider.testVehicleTesla()
        )
        Mockito.`when`(vehicleServiceClient.getAllVehicles()).thenReturn(vehicles)
        client = WebTestClient.bindToController(trackerServiceController)
            .configureClient()
            .baseUrl("http://tracker-service.com/tracking")
            .build()
        mongoTemplate.dropCollection(VehicleDataRecord::class.java)
        acuraDataRecords = emptyList<VehicleDataRecord>().toMutableList()
        audiDataRecords = emptyList<VehicleDataRecord>().toMutableList()
        teslaDataRecords = emptyList<VehicleDataRecord>().toMutableList()
    }

    @Test
    fun getVehiclesTrackingHistoryAcura() {
        insertVehicleDataRecordsForAcura()
        val stream = client.get().uri("/history/manufacturer/{manufacturerId}", "Acura")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(ManufacturerDataRecord::class.java)

        StepVerifier
            .create(stream.responseBody)
            .expectSubscription()
            .expectNext(acuraDataRecords[0].toManufacturerDataRecord())
            .expectNext(acuraDataRecords[1].toManufacturerDataRecord())
            .expectComplete()
            .verify()
    }

    @Test
    fun getVehiclesTrackingHistoryAudi() {
        insertVehicleDataRecordsForAudi()
        val stream = client.get().uri("/history/manufacturer/{manufacturerId}", "Audi")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(ManufacturerDataRecord::class.java)

        StepVerifier
            .create(stream.responseBody)
            .expectSubscription()
            .expectNext(audiDataRecords[0].toManufacturerDataRecord())
            .expectNext(audiDataRecords[1].toManufacturerDataRecord())
            .expectNext(audiDataRecords[2].toManufacturerDataRecord())
            .expectComplete()
            .verify()
    }

    @Test
    fun getVehiclesTrackingHistoryTesla() {
        insertVehicleDataRecordsForTesla()
        val stream = client.get().uri("/history/manufacturer/{manufacturerId}", "Tesla")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(ManufacturerDataRecord::class.java)

        StepVerifier
            .create(stream.responseBody)
            .expectSubscription()
            .expectNext(teslaDataRecords[0].toManufacturerDataRecord())
            .expectNext(teslaDataRecords[1].toManufacturerDataRecord())
            .expectNext(teslaDataRecords[2].toManufacturerDataRecord())
            .expectNext(teslaDataRecords[3].toManufacturerDataRecord())
            .expectComplete()
            .verify()
    }

    @Test
    fun getVehiclesTrackingHistoryForInvalidManufacturer() {
        insertVehicleDataRecordsForAcura()
        insertVehicleDataRecordsForAudi()
        insertVehicleDataRecordsForTesla()
        val stream = client.get().uri("/history/manufacturer/{manufacturerId}", "SomeInvalidId")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(ManufacturerDataRecord::class.java)

        StepVerifier
            .create(stream.responseBody)
            .expectSubscription()
            .expectComplete()
            .verify()
    }

    @Test
    fun getVehiclesTrackingStreamAcura() {
        val stream = client.get().uri("/manufacturer/{manufacturerId}", "Acura")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(ManufacturerDataRecord::class.java)

        StepVerifier
            .create(stream.responseBody)
            .expectSubscription()
            .then {
                mockReceivedAsynchronousVehicleDataRecords(
                    listOf(TestDataProvider.testVehicleDataRecordAcura(), TestDataProvider.testVehicleDataRecordAcura())
                )
            }
            .expectNext(pingManufacturerDataRecord)
            .expectNextCount(2)
            .thenCancel()
            .verify()
    }

    @Test
    fun getVehiclesTrackingStreamAudi() {
        val stream = client.get().uri("/manufacturer/{manufacturerId}", "Audi")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(ManufacturerDataRecord::class.java)

        StepVerifier
            .create(stream.responseBody)
            .expectSubscription()
            .then {
                mockReceivedAsynchronousVehicleDataRecords(
                    listOf(
                        TestDataProvider.testVehicleDataRecordAudi(), TestDataProvider.testVehicleDataRecordAudi(),
                        TestDataProvider.testVehicleDataRecordAudi(), TestDataProvider.testVehicleDataRecordAudi()
                    )
                )
            }
            .expectNext(pingManufacturerDataRecord)
            .expectNextCount(4)
            .thenCancel()
            .verify()
    }

    @Test
    fun getVehiclesTrackingStreamForInvalidManufacturer() {
        val stream = client.get().uri("/manufacturer/{manufacturerId}", "InvalidManufacturer")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(ManufacturerDataRecord::class.java)

        StepVerifier
            .create(stream.responseBody)
            .expectSubscription()
            .then {
                mockReceivedAsynchronousVehicleDataRecords(
                    listOf(
                        TestDataProvider.testVehicleDataRecordAcura(), TestDataProvider.testVehicleDataRecordAudi(),
                        TestDataProvider.testVehicleDataRecordTesla()
                    )
                )
            }
            .expectNext(pingManufacturerDataRecord)
            .expectNoEvent(Duration.ofSeconds(6))
            .thenCancel()
            .verify()
    }

    private fun insertVehicleDataRecordsForAcura() {
        acuraDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordAcura()).block()!!)
        acuraDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordAcura()).block()!!)
    }

    private fun insertVehicleDataRecordsForAudi() {
        audiDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordAudi()).block()!!)
        audiDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordAudi()).block()!!)
        audiDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordAudi()).block()!!)
    }

    private fun insertVehicleDataRecordsForTesla() {
        teslaDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordTesla()).block()!!)
        teslaDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordTesla()).block()!!)
        teslaDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordTesla()).block()!!)
        teslaDataRecords.add(repository.save(TestDataProvider.testVehicleDataRecordTesla()).block()!!)
    }

    private fun mockReceivedAsynchronousVehicleDataRecords(dataRecords: List<VehicleDataRecord>) {
        Flux.fromIterable(dataRecords)
            .delayElements(Duration.ofSeconds(2))
            .subscribe { receiver.receiveMessage(gson.toJson(it)) }
    }
}