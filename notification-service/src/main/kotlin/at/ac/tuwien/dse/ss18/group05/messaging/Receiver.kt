package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.service.IEmergencyServiceNotificationService
import at.ac.tuwien.dse.ss18.group05.service.IManufacturerNotificationService
import at.ac.tuwien.dse.ss18.group05.service.IVehicleNotificationService
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
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
class VehicleNotificationReceiver(private val service: IVehicleNotificationService, private val gson: Gson) {

    @RabbitListener(queues = ["#{vehicleQueue.name}"])
    fun receiveNotification(message: String) {
        val notification = gson.fromJson(message, IncomingVehicleNotification::class.java)
        service.handleIncomingVehicleNotification(notification)
    }
}

@Component
class EmsNotificationReceiver(private val service: IEmergencyServiceNotificationService, private val gson: Gson) {

    @RabbitListener(queues = ["#{emsQueue.name}"])
    fun receiveNotification(message: String) {
        val notification = gson.fromJson(message, EmergencyServiceNotification::class.java)
        service.handleEmsNotification(notification)
    }
}

@Component
class ManufacturerNotifictionReceiver(private val service: IManufacturerNotificationService, private val gson: Gson) {
    @RabbitListener(queues = ["#{manufacturerQueue.name}"])
    fun receiveNotification(message: String) {
        val notification = gson.fromJson(message, ManufacturerNotification::class.java)
        service.handleManufacturerNotification(notification)
    }
}
