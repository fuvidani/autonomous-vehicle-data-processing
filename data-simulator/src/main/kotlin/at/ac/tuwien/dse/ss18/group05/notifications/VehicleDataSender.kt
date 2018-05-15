package at.ac.tuwien.dse.ss18.group05.notifications

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceMessage
import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import com.google.gson.Gson
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class VehicleDataSender(
    private val rabbitTemplate: RabbitTemplate,
    private val gson: Gson
) {

    private val log = Logger.getLogger(this.javaClass.name)

    fun sendNotification(vehicleDataRecord: VehicleDataRecord) {
        val json = gson.toJson(vehicleDataRecord)
        rabbitTemplate.convertAndSend("vehicle-data-exchange", "vehicle.data.movement", json)
    }

    fun sendEmergencyStatusUpdate(message: EmergencyServiceMessage) {
        log.info("updating emergency status with message $message")
        val json = gson.toJson(message)
        rabbitTemplate.convertAndSend("vehicle-data-exchange", "ems.notification", json)
    }
}