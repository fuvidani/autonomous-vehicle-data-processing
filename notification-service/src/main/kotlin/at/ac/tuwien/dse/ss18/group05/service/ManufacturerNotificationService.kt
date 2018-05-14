package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.ManufacturerNotificationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

interface IManufacturerNotificationService {

    fun findAllHistoryNotifications(id: String): Flux<ManufacturerNotification>

    fun streamManufacturerNotifications(): Flux<ManufacturerNotification>
}

@Service
class ManufacturerNotificationService(
    private val receiver: Receiver<ManufacturerNotification>,
    private val repository: ManufacturerNotificationRepository
) : IManufacturerNotificationService {

    override fun findAllHistoryNotifications(id: String): Flux<ManufacturerNotification> {
        return repository.findByManufacturerId(id)
    }

    override fun streamManufacturerNotifications(): Flux<ManufacturerNotification> {
        return receiver.notificationStream()
    }
}