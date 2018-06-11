package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.VehicleNotificationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.logging.Logger


interface IVehicleNotificationService {

    /**
     * finding all notifications which occurred and the vehicle service stored
     *
     * @param id the id for which vehicle the notifications are searched
     */
    fun findHistoryNotificationsForVehicle(id: String): Flux<VehicleNotification>

    /**
     * streaming the notifications via hot flux to the client
     *
     * @param id the id for which vehicle the notifications are streamed
     */
    fun getStreamForVehicle(id: String): Flux<VehicleNotification>
}

@Service
class VehicleNotificationService(
    private val vehicleNotificationReceiver: Receiver<VehicleNotification>,
    private val repository: VehicleNotificationRepository
) : IVehicleNotificationService {

    private val log = Logger.getLogger(this.javaClass.name)

    override fun findHistoryNotificationsForVehicle(id: String): Flux<VehicleNotification> {
        log.info("finding history notifications for vehicle with id $id")
        return repository.findByVehicleIdentificationNumber(id)
    }

    override fun getStreamForVehicle(id: String): Flux<VehicleNotification> {
        log.info("retrieving stream for vehicle with id $id")
        return vehicleNotificationReceiver.notificationStream().filter { it.vehicleIdentificationNumber == id }
    }
}