package at.ac.tuwien.dse.ss18.group05.messaging.receiver

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceMessage
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
@Qualifier("EmergencyServiceMessageReceiver")
class EmergencyServiceMessageReceiver(private val processor: DataProcessor<EmergencyServiceMessage>, gson: Gson) :
    Receiver(gson) {

    /**
     * Abstract operation for receiving and presumably handling an incoming arbitrary message.
     *
     * @param message an arbitrary message in String format. Converters might be needed depending on the
     * encoding the message uses.
     */
    @RabbitListener(queues = ["#{emsNotificationQueue.name}"])
    override fun receiveMessage(message: String) {
        log.info("New emergency service message arrived $message")
        val serviceMessage = gson.fromJson<EmergencyServiceMessage>(message, EmergencyServiceMessage::class.java)
        processor.process(serviceMessage)
    }
}