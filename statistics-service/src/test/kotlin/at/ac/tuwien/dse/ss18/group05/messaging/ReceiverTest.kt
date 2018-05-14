package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.service.StatisticsService
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono

/**
 * <h4>About this class</h4>

 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(SpringRunner::class)
class ReceiverTest {

    private lateinit var receiver: Receiver
    @MockBean
    private lateinit var service: StatisticsService
    private val gson = Gson()

    @Before
    fun setUp() {
        receiver = StatisticsReceiver(gson, service)
    }

    @Test
    fun testReceiveVehicleDataShouldStreamItDown() {
        val report = TestDataProvider.testAccidentReport1()
        Mockito.`when`(service.create(report)).thenReturn(Mono.just(report))

        // TODO test Receiver.receiveMessage()
    }
}