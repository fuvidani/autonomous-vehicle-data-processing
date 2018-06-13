package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.StatisticsServiceApplication
import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.dto.AccidentReport
import at.ac.tuwien.dse.ss18.group05.repository.StatisticsRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import java.time.Duration

/**
 * <h4>About this class</h4>

 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(SpringRunner::class)
@SpringBootTest(value = ["application.yml"], classes = [StatisticsServiceApplication::class])
class StatisticsServiceDocumentationTest {

    @Rule
    @JvmField
    final val restDocumentation = JUnitRestDocumentation()
    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin
    @MockBean
    private lateinit var repository: StatisticsRepository
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    private lateinit var client: WebTestClient
    @Autowired
    private lateinit var statisticsController: StatisticsController

    @Before
    fun setUp() {
        val reports = listOf(
            TestDataProvider.testAccidentReport1(),
            TestDataProvider.testAccidentReport2()
        )
        Mockito.`when`(repository.findBy()).thenReturn(Flux.fromIterable(reports))
        client = WebTestClient.bindToController(statisticsController)
            .configureClient()
            .baseUrl("http://statistics-service.com/statistics")
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .responseTimeout(Duration.ofSeconds(15))
            .build()
        mongoTemplate.dropCollection(AccidentReport::class.java)
        val options = CollectionOptions.empty().capped().size(5242880).maxDocuments(5000)
        mongoTemplate.createCollection("statistics", options)
    }

    @Test
    fun getAllAccidentRecords() {
        client.get().uri("/accidents")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "accident-report-stream",
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("id")
                            .type(JsonFieldType.STRING)
                            .description("The accident report's ID"),
                        PayloadDocumentation.fieldWithPath("accidentId")
                            .type(JsonFieldType.STRING)
                            .description("ID of the accident"),
                        PayloadDocumentation.fieldWithPath("vehicleMetaData.identificationNumber")
                            .type(JsonFieldType.STRING)
                            .description("The vehicle's world-wide unique Vehicle Identification Number"),
                        PayloadDocumentation.fieldWithPath("vehicleMetaData.model")
                            .type(JsonFieldType.STRING)
                            .description("The vehicle's type of model"),
                        PayloadDocumentation.fieldWithPath("location.lat")
                            .type(JsonFieldType.NUMBER)
                            .description("The latitude coordinate of the accident"),
                        PayloadDocumentation.fieldWithPath("location.lon")
                            .type(JsonFieldType.NUMBER)
                            .description("The longitude coordinate of the accident"),
                        PayloadDocumentation.fieldWithPath("passengers")
                            .type(JsonFieldType.NUMBER)
                            .description("Number of passenger in the vehicle"),
                        PayloadDocumentation.fieldWithPath("timestampOfAccident")
                            .type(JsonFieldType.NUMBER)
                            .description("Time of accident in milliseconds (UTC)"),
                        PayloadDocumentation.fieldWithPath("emergencyResponseInMillis")
                            .type(JsonFieldType.NUMBER)
                            .description("Duration of emergency response in milliseconds"),
                        PayloadDocumentation.fieldWithPath("durationOfSiteClearingInMillis")
                            .type(JsonFieldType.NUMBER)
                            .description("Duration of site clearing in milliseconds")

                    )
                )
            )
    }
}