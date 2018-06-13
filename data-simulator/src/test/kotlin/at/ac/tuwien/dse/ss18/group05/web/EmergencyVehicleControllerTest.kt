package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.DataSimulatorApplication
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceMessage
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.notifications.VehicleDataSender
import at.ac.tuwien.dse.ss18.group05.scenario.VehicleSimulator
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
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
@SpringBootTest(value = ["application.yml"], classes = [DataSimulatorApplication::class])
class EmergencyVehicleControllerTest {

    @Rule
    @JvmField
    final val restDocumentation = JUnitRestDocumentation()
    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin
    @Suppress("unused")
    @MockBean
    private lateinit var simulator: VehicleSimulator
    @MockBean
    private lateinit var vehicleDataSender: VehicleDataSender
    private lateinit var emergencyVehicleController: EmergencyVehicleController
    private lateinit var client: WebTestClient

    @Before
    fun setUp() {
        emergencyVehicleController = EmergencyVehicleController(vehicleDataSender)
        client = WebTestClient.bindToController(emergencyVehicleController)
            .configureClient()
            .baseUrl("http://data-simulator.com/datasimulation")
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .responseTimeout(Duration.ofSeconds(15))
            .build()
    }

    @Test
    fun testEmergencyServiceStatusUpdateShouldReturnOk() {
        val message = EmergencyServiceMessage(System.currentTimeMillis(), "accidentId", EmergencyServiceStatus.ARRIVED)
        client.post().uri("/updatestatus")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(message), EmergencyServiceMessage::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "ems-update-status",
                    requestFields(
                        fieldWithPath("timestamp")
                            .type(JsonFieldType.NUMBER)
                            .description("The timestamp of the message"),
                        fieldWithPath("accidentId")
                            .type(JsonFieldType.STRING)
                            .description("ID of the accident this message corresponds to"),
                        fieldWithPath("status")
                            .type(JsonFieldType.STRING)
                            .description("The emergency service's status in the scope of this accident")
                    )
                )
            )
    }
}