package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.DataSimulatorApplication
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
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
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
class VehicleControllerTest {

    @Rule
    @JvmField
    final val restDocumentation = JUnitRestDocumentation()
    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin
    @Suppress("unused")
    @MockBean
    private lateinit var simulator: VehicleSimulator
    private lateinit var vehicleController: VehicleController
    private lateinit var client: WebTestClient

    @Before
    fun setUp() {
        vehicleController = VehicleController(simulator)
        client = WebTestClient.bindToController(vehicleController)
            .configureClient()
            .baseUrl("http://data-simulator.com/datasimulation")
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .responseTimeout(Duration.ofSeconds(15))
            .build()
    }

    @Test
    fun testSimulateCrashShouldReturnOk() {
        client.post().uri("/simulatecrash")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun testSimulateNearCrashShouldReturnOk() {
        client.post().uri("/simulatenearcrash")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun testResetSimulateShouldReturnOk() {
        client.post().uri("/reset")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk
    }
}