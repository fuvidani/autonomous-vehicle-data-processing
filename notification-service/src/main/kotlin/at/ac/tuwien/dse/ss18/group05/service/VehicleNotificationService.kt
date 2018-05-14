package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.repository.VehicleNotificationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
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

interface IVehicleNotificationService {

    fun handleIncomingVehicleNotification(incomingVehicleNotification: IncomingVehicleNotification)

    fun getNotificationForVehicle(id: String): Flux<VehicleNotification>

    fun findAllNotificationsForVehicle(id: String): Flux<VehicleNotification>

    fun closeStream()
}

@Service
class VehicleNotificationService(private val repository: VehicleNotificationRepository) : IVehicleNotificationService {

    private val log = Logger.getLogger(this.javaClass.name)

    private val processor = TopicProcessor.builder<VehicleNotification>()
            .autoCancel(false)
            .share(true)
            .name("vehicle_notification_topic")
            .build()

    override fun closeStream() {
        processor.onComplete()
    }

    override fun findAllNotificationsForVehicle(id: String): Flux<VehicleNotification> {
        return repository.findByvehicleIdentificationNumber(id)
    }

    override fun getNotificationForVehicle(id: String): Flux<VehicleNotification> {
        return processor.filter { it.vehicleIdentificationNumber == id }
    }

    override fun handleIncomingVehicleNotification(incomingVehicleNotification: IncomingVehicleNotification) {
        log.info("handling incoming notification ${incomingVehicleNotification}")
        handleNotifications(incomingVehicleNotification, incomingVehicleNotification.concernedNearByVehicles)
        handleFieldsForFarAwayVehicles(incomingVehicleNotification)
        handleNotifications(incomingVehicleNotification, incomingVehicleNotification.concernedFarAwayVehicles)

    }

    private fun handleFieldsForFarAwayVehicles(incomingVehicleNotification: IncomingVehicleNotification){
        incomingVehicleNotification.targetSpeed = null
        incomingVehicleNotification.specialWarning = null
    }

    private fun handleNotifications(incomingVehicleNotification: IncomingVehicleNotification, ids: Array<String>) {
        ids.forEach {
            val vehicleNotification = mapIncomingNotificationToVehicle(it, incomingVehicleNotification)
            handleNotification(vehicleNotification)
        }
    }

    private fun handleNotification(vehicleNotification: VehicleNotification) {
        repository.save(vehicleNotification).subscribe{processor.onNext(it)}
    }

    private fun mapIncomingNotificationToVehicle(id: String, incomingVehicleNotification: IncomingVehicleNotification): VehicleNotification {
        return VehicleNotification(vehicleId = id, incomingVehicleNotification = incomingVehicleNotification)
    }
}