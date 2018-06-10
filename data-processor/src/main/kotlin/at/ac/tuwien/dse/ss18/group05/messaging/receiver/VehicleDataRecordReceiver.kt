package at.ac.tuwien.dse.ss18.group05.messaging.receiver

import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.processing.DataProcessor
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Qualifier("VehicleDataRecordReceiver")
class VehicleDataRecordReceiver(private val processor: DataProcessor<VehicleDataRecord>, gson: Gson) : Receiver(gson) {

    @RabbitListener(queues = ["#{vehicleMovementQueue.name}"])
    override fun receiveMessage(message: String) {

        val vehicleDataRecord = gson.fromJson<VehicleDataRecord>(message, VehicleDataRecord::class.java)

        println()
        log.info("vehicle data record $vehicleDataRecord")

        processor.process(vehicleDataRecord)
    }
}