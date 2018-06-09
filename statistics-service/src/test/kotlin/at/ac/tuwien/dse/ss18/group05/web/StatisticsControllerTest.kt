package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.StatisticsServiceApplication
import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.dto.AccidentReport
import at.ac.tuwien.dse.ss18.group05.repository.StatisticsRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import org.springframework.data.mongodb.core.CollectionOptions
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
class StatisticsControllerTest {

    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin
    @Autowired
    private lateinit var repository: StatisticsRepository
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    @Autowired
    private lateinit var statisticsController: StatisticsController
    private lateinit var client: WebTestClient
    private lateinit var reports: MutableList<AccidentReport>

    @Before
    fun setUp() {
        client = WebTestClient.bindToController(statisticsController)
            .configureClient()
            .baseUrl("http://statistics-service.com/statistics")
            .build()
        mongoTemplate.dropCollection(AccidentReport::class.java)
        val options = CollectionOptions.empty().capped().size(5242880).maxDocuments(5000)
        mongoTemplate.createCollection("statistics", options)
        reports = emptyList<AccidentReport>().toMutableList()
    }

    @Test
    fun getAllAccidentRecords() {
        insertAccidentReports()
        val stream = client.get().uri("/accidents")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(AccidentReport::class.java)

        StepVerifier
            .create(stream.responseBody)
            .expectSubscription()
            .expectNext(reports[0])
            .expectNext(reports[1])
            .expectNoEvent(Duration.ofSeconds(3))
            .thenCancel()
            .verify()
    }

    @Test
    fun getAllAccidentRecordsForEmptyList() {
        val stream = client.get().uri("/accidents")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(AccidentReport::class.java)

        StepVerifier
            .create(stream.responseBody)
            .expectSubscription()
            .expectNoEvent(Duration.ofSeconds(3))
            .thenCancel()
            .verify()
    }

    private fun insertAccidentReports() {
        reports.add(repository.save(TestDataProvider.testAccidentReport1()).block()!!)
        reports.add(repository.save(TestDataProvider.testAccidentReport2()).block()!!)
    }
}