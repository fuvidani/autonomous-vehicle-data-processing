package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.service.IEmergencyServiceNotificationService
import at.ac.tuwien.dse.ss18.group05.service.IManufacturerNotificationService
import at.ac.tuwien.dse.ss18.group05.service.IVehicleNotificationService
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
class VehicleNotificationReceiver(private val service: IVehicleNotificationService) {

    @RabbitListener(queues = ["#{vehicleQueue.name}"])
    fun receiveNotification(notification: IncomingVehicleNotification) {
        service.handleIncomingVehicleNotification(notification)
    }
}

@Component
class EmsNotificationReceiver(private val service: IEmergencyServiceNotificationService) {

    @RabbitListener(queues = ["#{emsQueue.name}"])
    fun receiveNotification(notification: EmergencyServiceNotification) {
        service.handleEmsNotification(notification)
    }
}

@Component
class ManufacturerNotifictionReceiver(private val service: IManufacturerNotificationService) {
    @RabbitListener(queues = ["#{manufacturerQueue.name}"])
    fun receiveNotification(notification: ManufacturerNotification) {
        service.handleManufacturerNotification(notification)
    }
}
