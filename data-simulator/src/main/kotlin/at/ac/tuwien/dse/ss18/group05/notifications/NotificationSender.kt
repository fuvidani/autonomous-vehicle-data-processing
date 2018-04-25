package at.ac.tuwien.dse.ss18.group05.notifications

import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import com.google.gson.Gson
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class NotificationSender(
    private val rabbitTemplate: RabbitTemplate,
    private val gson: Gson
) {

    fun sendNotification(notification: VehicleDataRecord) {
        val json = gson.toJson(Object())
        rabbitTemplate.convertAndSend("vehicle-data-exchange", "notifications.event", json)
    }
}