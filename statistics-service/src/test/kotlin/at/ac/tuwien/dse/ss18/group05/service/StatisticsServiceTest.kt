package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.repository.StatisticsRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

/**
 * <h4>About this class</h4>

 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(SpringRunner::class)
class StatisticsServiceTest {

    private lateinit var statisticsService: IStatisticsService
    @MockBean
    private lateinit var repository: StatisticsRepository

    @Before
    fun setUp() {
        val reports = listOf(
            TestDataProvider.testAccidentReport1(),
            TestDataProvider.testAccidentReport2()
        )
        Mockito.`when`(repository.findBy()).thenReturn(Flux.fromIterable(reports))
        Mockito.`when`(repository.save(reports[0])).thenReturn(Mono.just(reports[0]))
        Mockito.`when`(repository.save(reports[1])).thenReturn(Mono.just(reports[1]))

        statisticsService = StatisticsService(repository)
    }

    @Test
    fun testGetAllAccidentReportsShouldReturnCorrectList() {
        StepVerifier
            .create(statisticsService.findAll())
            .expectSubscription()
            .expectNext(TestDataProvider.testAccidentReport1())
            .expectNext(TestDataProvider.testAccidentReport2())
            .expectComplete()
            .verify()
    }

    @Test
    fun testCreateAccidentReportShouldReturnCorrectReport1() {
        StepVerifier
            .create(statisticsService.create(TestDataProvider.testAccidentReport1()))
            .expectSubscription()
            .expectNext(TestDataProvider.testAccidentReport1())
            .expectComplete()
            .verify()
    }

    @Test
    fun testCreateAccidentReportShouldReturnCorrectReport2() {
        StepVerifier
            .create(statisticsService.create(TestDataProvider.testAccidentReport2()))
            .expectSubscription()
            .expectNext(TestDataProvider.testAccidentReport2())
            .expectComplete()
            .verify()
    }
}