package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.ManufacturerNotificationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.logging.Logger

interface IManufacturerNotificationService {

    fun findAllHistoryNotifications(id: String): Flux<ManufacturerNotification>

    fun streamManufacturerNotifications(id: String): Flux<ManufacturerNotification>
}

@Service
class ManufacturerNotificationService(
    private val receiver: Receiver<ManufacturerNotification>,
    private val repository: ManufacturerNotificationRepository
) : IManufacturerNotificationService {

    private val log = Logger.getLogger(this.javaClass.name)

    override fun findAllHistoryNotifications(id: String): Flux<ManufacturerNotification> {
        log.info("returning all history notifications for manufacturer")
        return repository.findByManufacturerId(id)
    }

    override fun streamManufacturerNotifications(id: String): Flux<ManufacturerNotification> {
        log.info("retrieving stream for manufacturer with id $id")
        return receiver.notificationStream().filter { v -> v.manufacturerId == id }
    }
}