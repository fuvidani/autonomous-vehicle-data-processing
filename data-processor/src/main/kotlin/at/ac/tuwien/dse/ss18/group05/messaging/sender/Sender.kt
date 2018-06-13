package at.ac.tuwien.dse.ss18.group05.messaging.sender

import com.google.gson.Gson
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
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
class Sender(
    private val rabbitTemplate: RabbitTemplate,
    private val gson: Gson
) {

    private val log: Logger = Logger.getLogger(this.javaClass.name)

    /**
     * Places an arbitrary message on the asynchronous message queue with the specified
     * routing key.
     *
     * @param message an arbitrary message that must be published
     * @param routingKey the routing key defining the destination of the message (topic)
     */
    fun sendMessage(message: Any, routingKey: String) {
        log.info("Placing message of type ${message.javaClass.simpleName} on bus")
        val json = gson.toJson(message)
        rabbitTemplate.convertAndSend("vehicle-data-exchange", routingKey, json)
    }
}