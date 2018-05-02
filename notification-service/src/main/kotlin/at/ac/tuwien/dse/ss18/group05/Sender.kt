package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class Sender() {
    @Autowired
    private lateinit var template: RabbitTemplate

    @Autowired
    private lateinit var topic: TopicExchange

    @Scheduled(fixedDelay = 1000)
    private fun send() {
        val topics = arrayListOf("notifications.vehicle", "notifications.ems", "notifications.manufacturer")

        val notification = EmergencyServiceNotification("", accidentId = "acc", timeStamp = 12L, location = GpsLocation(43.4, 12.2), model = "model", passengers = 2)
        template.convertAndSend(topic.getName(), topics[1], notification)
    }
}