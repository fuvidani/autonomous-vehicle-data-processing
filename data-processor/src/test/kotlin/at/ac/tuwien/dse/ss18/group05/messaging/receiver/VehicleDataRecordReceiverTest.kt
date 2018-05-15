package at.ac.tuwien.dse.ss18.group05.messaging.receiver

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.processing.DataProcessor
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

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
class VehicleDataRecordReceiverTest {

    private lateinit var receiver: Receiver
    @MockBean
    private lateinit var dataProcessor: DataProcessor<VehicleDataRecord>
    private val gson = Gson()

    @Before
    fun setUp() {
        receiver = VehicleDataRecordReceiver(dataProcessor, gson)
    }

    @Test
    fun testReceiveMessageShouldInvokeProcessor() {
        val json = gson.toJson(TestDataProvider.testVehicleDataRecordAcura())
        receiver.receiveMessage(json)
        Mockito.verify(dataProcessor).process(TestDataProvider.testVehicleDataRecordAcura())
    }
}