package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.repository.VehicleNotificationRepository
import org.springframework.stereotype.Service
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

interface IVehicleNotificationService {

    fun handleIncomingVehicleNotification(incomingVehicleNotification: IncomingVehicleNotification)

    fun getNotificationForVehicle(id: String): Flux<VehicleNotification>

    fun findAllNotificationsForVehicle(id: String): Flux<VehicleNotification>
}

@Service
class VehicleNotificationService(private val repository: VehicleNotificationRepository) : IVehicleNotificationService {

    private val processor = TopicProcessor.builder<VehicleNotification>()
            .autoCancel(false)
            .share(true)
            .name("vehicle_notification_topic")
            .build()

    override fun findAllNotificationsForVehicle(id: String): Flux<VehicleNotification> {
        return repository.findByvehicleIdentificationNumber(id)
    }

    override fun getNotificationForVehicle(id: String): Flux<VehicleNotification> {
        return processor.filter { it.vehicleIdentificationNumber == id }
    }

    override fun handleIncomingVehicleNotification(incomingVehicleNotification: IncomingVehicleNotification) {
        saveAllNotifications(incomingVehicleNotification)
        streamAllNotifications(incomingVehicleNotification)
    }

    private fun streamAllNotifications(incomingVehicleNotification: IncomingVehicleNotification) {
        streamNotifications(incomingVehicleNotification, incomingVehicleNotification.concernedFarAwayVehicles)
        streamNotifications(incomingVehicleNotification, incomingVehicleNotification.concernedNearByVehicles)
    }

    private fun saveAllNotifications(incomingVehicleNotification: IncomingVehicleNotification) {
        saveNotifications(incomingVehicleNotification, incomingVehicleNotification.concernedFarAwayVehicles)
        saveNotifications(incomingVehicleNotification, incomingVehicleNotification.concernedNearByVehicles)
    }

    private fun saveNotifications(incomingVehicleNotification: IncomingVehicleNotification, ids: Array<String>) {
        ids.forEach {
            val vehicleNotification = mapIncomingNotificationToVehicle(it, incomingVehicleNotification)
            saveNotification(vehicleNotification)
        }
    }

    private fun saveNotification(vehicleNotification: VehicleNotification) {
        repository.save(vehicleNotification).subscribe()
    }

    private fun streamNotifications(incomingVehicleNotification: IncomingVehicleNotification, ids: Array<String>) {
        ids.forEach {
            val vehicleNotification = mapIncomingNotificationToVehicle(it, incomingVehicleNotification)
            streamNotification(vehicleNotification)
        }
    }

    private fun streamNotification(vehicleNotification: VehicleNotification) {
        processor.onNext(vehicleNotification)
    }

    private fun mapIncomingNotificationToVehicle(id: String, incomingVehicleNotification: IncomingVehicleNotification): VehicleNotification {
        return VehicleNotification(id, incomingVehicleNotification)
    }
}