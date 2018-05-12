package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.repository.VehicleDataRecordRepository
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
import reactor.util.concurrent.Queues
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
    private val repository: VehicleDataRecordRepository
) : Receiver {

    private val processor = TopicProcessor.builder<VehicleDataRecord>()
        .autoCancel(false)
        .share(true)
        .name("something")
        .bufferSize(Queues.SMALL_BUFFER_SIZE)
        .build()
    private val log = Logger.getLogger(this.javaClass.name)

    @RabbitListener(queues = ["#{vehicleQueue.name}"])
    override fun receiveMessage(message: String) {
        log.info("New vehicle data record arrived")
        val vehicleDataRecord = gson.fromJson<VehicleDataRecord>(message, VehicleDataRecord::class.java)
        repository.save(vehicleDataRecord).subscribe()
        processor.onNext(vehicleDataRecord)
    }

    override fun recordStream(): Flux<VehicleDataRecord> {
        return processor
    }
}