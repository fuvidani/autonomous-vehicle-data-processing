package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.repository.VehicleNotificationRepository
import at.ac.tuwien.dse.ss18.group05.service.IEmergencyServiceNotificationService
import at.ac.tuwien.dse.ss18.group05.service.IManufacturerNotificationService
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */

interface Receiver<T> {

    fun receiveMessage(message: String)

    fun notificationStream(): Flux<T>
}

@Component
class VehicleNotificationReceiver(
        private val repository: VehicleNotificationRepository,
        private val processor: TopicProcessor<VehicleNotification>,
        private val gson: Gson) : Receiver<VehicleNotification> {

    @RabbitListener(queues = ["#{vehicleQueue.name}"])
    override fun receiveMessage(message: String) {
        val incomingVehicleNotification = gson.fromJson(message, IncomingVehicleNotification::class.java)
        handleNotifications(incomingVehicleNotification, incomingVehicleNotification.concernedNearByVehicles)
        handleFieldsForFarAwayVehicles(incomingVehicleNotification)
        handleNotifications(incomingVehicleNotification, incomingVehicleNotification.concernedFarAwayVehicles)
    }

    private fun handleFieldsForFarAwayVehicles(incomingVehicleNotification: IncomingVehicleNotification) {
        incomingVehicleNotification.targetSpeed = null
        incomingVehicleNotification.specialWarning = null
    }

    private fun handleNotifications(incomingVehicleNotification: IncomingVehicleNotification, ids: Array<String>) {
        ids.forEach {
            val vehicleNotification = VehicleNotification(vehicleId = it, incomingVehicleNotification = incomingVehicleNotification)
            repository.save(vehicleNotification).subscribe { processor.onNext(it) }

        }
    }

    override fun notificationStream(): Flux<VehicleNotification> {
        return processor
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
