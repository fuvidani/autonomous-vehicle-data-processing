package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.repository.VehicleDataRecordRepository
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
import java.time.ZonedDateTime
import java.util.logging.Logger

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
class VehicleDataRecordReceiver(
    private val gson: Gson,
    private val repository: VehicleDataRecordRepository,
    private val processor: TopicProcessor<VehicleDataRecord>
) : Receiver {

    private var lastLog = ZonedDateTime.now()
    private val log = Logger.getLogger(this.javaClass.name)

    /**
     * Receives the provided message.
     *
     * @param message an arbitrary message in String format
     */
    @RabbitListener(queues = ["#{vehicleQueue.name}"])
    override fun receiveMessage(message: String) {
        val vehicleDataRecord = gson.fromJson<VehicleDataRecord>(message, VehicleDataRecord::class.java)
        logIfNeeded(vehicleDataRecord)
        processor.onNext(repository.save(vehicleDataRecord).block()!!)
    }

    private fun logIfNeeded(vehicleDataRecord: VehicleDataRecord) {
        if (vehicleDataRecord.eventInformation == EventInformation.NEAR_CRASH || vehicleDataRecord.eventInformation == EventInformation.CRASH) {
            log.info("vehicle data record with event information received $vehicleDataRecord")
        } else {
            if (lastLog.plusSeconds(1).isBefore(ZonedDateTime.now())) {
                println()
                log.info("vehicle data record $vehicleDataRecord")
                lastLog = ZonedDateTime.now()
            }
        }
    }

    /**
     * Returns a stream of vehicle data records received by this
     * receiver.
     *
     * @return Flux of vehicle data records
     */
    override fun recordStream(): Flux<VehicleDataRecord> {
        return processor
    }
}