package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.repository.VehicleDataRecordRepository
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.ReplayProcessor
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
class ReceiverTest {

    private lateinit var receiver: Receiver
    @MockBean
    private lateinit var repository: VehicleDataRecordRepository
    private val gson = Gson()

    @Before
    fun setUp() {
        val processor = ReplayProcessor.create<VehicleDataRecord>(2)
        receiver = VehicleDataRecordReceiver(gson, repository, processor)
    }

    @Test
    fun testReceiveVehicleDataShouldStreamItDown() {
        val record = TestDataProvider.testVehicleDataRecordAcura()
        Mockito.`when`(repository.save(record)).thenReturn(Mono.just(record))
        StepVerifier
            .create(receiver.recordStream())
            .expectSubscription()
            .then { receiver.receiveMessage(gson.toJson(record)) }
            .expectNext(record)
            .thenCancel()
            .verify(Duration.ofSeconds(5))
    }

    @Test
    fun testReceiveMultipleVehicleDataShouldStreamThemDown() {
        val audi = TestDataProvider.testVehicleDataRecordAudi()
        val acura = TestDataProvider.testVehicleDataRecordAcura()
        val tesla = TestDataProvider.testVehicleDataRecordTesla()
        Mockito.`when`(repository.save(audi)).thenReturn(Mono.just(audi))
        Mockito.`when`(repository.save(acura)).thenReturn(Mono.just(acura))
        Mockito.`when`(repository.save(tesla)).thenReturn(Mono.just(tesla))
        val records = listOf(audi, acura, tesla)

        StepVerifier
            .create(receiver.recordStream())
            .expectSubscription()
            .then {
                Flux.fromIterable(records)
                    .delayElements(Duration.ofSeconds(2))
                    .subscribe { receiver.receiveMessage(gson.toJson(it)) }
            }
            .expectNoEvent(Duration.ofMillis(1800))
            .expectNext(audi)
            .expectNoEvent(Duration.ofMillis(1800))
            .expectNext(acura)
            .expectNoEvent(Duration.ofMillis(1800))
            .expectNext(tesla)
            .expectNoEvent(Duration.ofSeconds(1))
            .thenCancel()
            .verify()
    }
}