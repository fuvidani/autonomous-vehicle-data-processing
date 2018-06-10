package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.repository.VehicleDataRecordRepository
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
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

    private val log = Logger.getLogger(this.javaClass.name)

    @RabbitListener(queues = ["#{vehicleQueue.name}"])
    override fun receiveMessage(message: String) {
        val vehicleDataRecord = gson.fromJson<VehicleDataRecord>(message, VehicleDataRecord::class.java)
        println()
        log.info("vehicle data record $vehicleDataRecord")
        processor.onNext(repository.save(vehicleDataRecord).block()!!)
    }

    override fun recordStream(): Flux<VehicleDataRecord> {
        return processor
    }
}