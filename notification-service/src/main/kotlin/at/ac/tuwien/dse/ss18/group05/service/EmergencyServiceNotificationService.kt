package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.repository.EmergencyServiceNotificationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
import reactor.core.publisher.toFlux
import reactor.util.concurrent.Queues

interface IEmergencyServiceNotificationService {

    fun handleEmsNotification(emergencyServiceNotification: EmergencyServiceNotification)

    fun findAll(): Flux<EmergencyServiceNotification>

    fun streamEmsNotifications(): Flux<EmergencyServiceNotification>
}

@Service
class EmergencyServiceNotificationService(private val repository: EmergencyServiceNotificationRepository) : IEmergencyServiceNotificationService {

    private val processor = TopicProcessor.builder<EmergencyServiceNotification>()
            .autoCancel(false)
            .share(true)
            .name("ems_notification_topic")
            .bufferSize(Queues.SMALL_BUFFER_SIZE)
            .build()

    override fun findAll(): Flux<EmergencyServiceNotification> {
        return repository.findAll()
    }

    override fun streamEmsNotifications(): Flux<EmergencyServiceNotification> {
        return processor.toFlux()
    }

    override fun handleEmsNotification(emergencyServiceNotification: EmergencyServiceNotification) {
        saveNotification(emergencyServiceNotification)
        streamNotification(emergencyServiceNotification)
    }

    private fun saveNotification(emergencyServiceNotification: EmergencyServiceNotification) {
        repository.save(emergencyServiceNotification).subscribe()
    }

    private fun streamNotification(emergencyServiceNotification: EmergencyServiceNotification) {
        processor.onNext(emergencyServiceNotification)
    }
}