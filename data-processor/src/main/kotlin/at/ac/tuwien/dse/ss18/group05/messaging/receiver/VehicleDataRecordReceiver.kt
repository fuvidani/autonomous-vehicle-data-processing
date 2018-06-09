package at.ac.tuwien.dse.ss18.group05.messaging.receiver

import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.processing.DataProcessor
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

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

    private var lastLog = ZonedDateTime.now()

    /**
     * Abstract operation for receiving and presumably handling an incoming arbitrary message.
     *
     * @param message an arbitrary message in String format. Converters might be needed depending on the
     * encoding the message uses.
     */
    @RabbitListener(queues = ["#{vehicleQueue.name}"])
    override fun receiveMessage(message: String) {
        val vehicleDataRecord = gson.fromJson<VehicleDataRecord>(message, VehicleDataRecord::class.java)
        logMessageIfNecessary(vehicleDataRecord)
        processor.process(vehicleDataRecord)
    }

    private fun logMessageIfNecessary(vehicleDataRecord: VehicleDataRecord) {
        if (vehicleDataRecord.eventInformation == EventInformation.NEAR_CRASH || vehicleDataRecord.eventInformation == EventInformation.CRASH) {
            log.info("CRASH - vehicle data record with event information received $vehicleDataRecord")
        } else {
            if (lastLog.plusSeconds(1).isBefore(ZonedDateTime.now())) {
                println()
                log.info("vehicle data record $vehicleDataRecord")
                lastLog = ZonedDateTime.now()
            }
        }
    }
}