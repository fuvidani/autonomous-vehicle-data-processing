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

    /**
     * sending the vehicle data to the data processor and the tracking service
     * converting it to a json object to not hit serialization issues
     *
     * @param vehicleDataRecord the record information to send via rabbit
     */
    @Synchronized
    fun sendVehicleDataRecord(vehicleDataRecord: VehicleDataRecord) {
        val json = gson.toJson(vehicleDataRecord)
        //use two separate routing keys for data-processor and tracker service because
        rabbitTemplate.convertAndSend("vehicle-data-exchange-2", "vehicle.data.movement", json)
        rabbitTemplate.convertAndSend("vehicle-data-exchange", "vehicle-data-tracking", json)
    }

    /**
     * putting the ems update message on the rabbit stream for further processing
     * converting it to a json object to not hit serialization issues
     *
     * @param message the object to send via rabbit
     */
    fun sendEmergencyStatusUpdate(message: EmergencyServiceMessage) {
        log.info("updating emergency status with message $message")
        val json = gson.toJson(message)
        rabbitTemplate.convertAndSend("vehicle-data-exchange-2", "ems.notification", json)
    }
}