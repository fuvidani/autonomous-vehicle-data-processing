package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.repository.ManufacturerNotificationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
import reactor.util.concurrent.Queues

interface IManufacturerNotificationService {

    fun handleManufacturerNotification(manufacturerNotification: ManufacturerNotification)

    fun findAllForManufacturer(id: String): Flux<ManufacturerNotification>

    fun streamManufacturerNotifications(): Flux<ManufacturerNotification>
}

@Service
class ManufacturerNotificationService(private val repository: ManufacturerNotificationRepository) : IManufacturerNotificationService {

    private val processor = TopicProcessor.builder<ManufacturerNotification>()
            .autoCancel(false)
            .share(true)
            .name("manufacturer_notification_topic")
            .bufferSize(Queues.SMALL_BUFFER_SIZE)
            .build()

    override fun handleManufacturerNotification(manufacturerNotification: ManufacturerNotification) {
        saveNotification(manufacturerNotification)
        streamNotification(manufacturerNotification)
    }

    override fun findAllForManufacturer(id: String): Flux<ManufacturerNotification> {
        return repository.findByManufacturerId(id)
    }

    override fun streamManufacturerNotifications(): Flux<ManufacturerNotification> {
        return processor
    }

    private fun saveNotification(notification: ManufacturerNotification) {
        repository.save(notification).subscribe()
    }

    private fun streamNotification(notification: ManufacturerNotification) {
        processor.onNext(notification)
    }
}