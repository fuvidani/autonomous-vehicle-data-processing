package at.ac.tuwien.dse.ss18.group05.notifications

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceMessage
import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import com.google.gson.Gson
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.util.logging.Logger

@Component
class VehicleDataSender(
    private val rabbitTemplate: RabbitTemplate,
    private val gson: Gson
) {

    private var lastLog = ZonedDateTime.now()
    private val log = Logger.getLogger(this.javaClass.name)

    @Synchronized
    fun sendVehicleDataRecord(vehicleDataRecord: VehicleDataRecord) {
        val json = gson.toJson(vehicleDataRecord)

        if (vehicleDataRecord.eventInformation == EventInformation.CRASH) {
            println()
            log.info("vehicle data record $vehicleDataRecord")
        } else if (lastLog.plusSeconds(3).isBefore(ZonedDateTime.now())) {
            println()
            log.info("vehicle data record $vehicleDataRecord")
            lastLog = ZonedDateTime.now()
        }

        rabbitTemplate.convertAndSend("vehicle-data-exchange", "vehicle.data.movement", json)
    }

    fun sendEmergencyStatusUpdate(message: EmergencyServiceMessage) {
        log.info("updating emergency status with message $message")
        val json = gson.toJson(message)
        rabbitTemplate.convertAndSend("vehicle-data-exchange", "ems.notification", json)
    }
}