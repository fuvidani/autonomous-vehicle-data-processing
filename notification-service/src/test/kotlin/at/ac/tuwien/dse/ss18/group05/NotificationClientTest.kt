package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.dto.Notification
import at.ac.tuwien.dse.ss18.group05.service.NotificationService
import at.ac.tuwien.dse.ss18.group05.web.NotificationController
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux

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
@SpringBootTest(value = ["application.yml"], classes = [NotificationServiceTestApplication::class])
class NotificationClientTest {

    @Rule
    @JvmField
    final val restDocumentation = JUnitRestDocumentation()
    @MockBean
    private lateinit var notificationService: NotificationService
    private lateinit var testClient: WebTestClient

    // Kotlin<->Java Mockito type inference workaround
    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Before
    fun setUp() {
        Mockito.`when`(notificationService.getNotificationForVehicle(any(String::class.java)))
            .thenReturn(
                Flux.just(Notification("", emptyList(), ""))
            )
        testClient = WebTestClient.bindToController(NotificationController(notificationService))
            .configureClient()
            .filter(documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    fun testGetNotificationsShouldReturnSample() {
        testClient.get()
            .uri("/notifications/{id}", "vehicleId")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "notifications",
                    responseFields(
                        fieldWithPath("id").description("The notification's ID"),
                        fieldWithPath("concernedVehicles").description("List of vehicle IDs as recipients"),
                        fieldWithPath("message").description("The notification's message")
                    )
                )
            )
    }
}