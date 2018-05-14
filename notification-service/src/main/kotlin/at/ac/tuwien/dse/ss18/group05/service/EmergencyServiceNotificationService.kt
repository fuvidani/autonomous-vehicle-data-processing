package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.repository.EmergencyServiceNotificationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
import java.util.logging.Logger

interface IEmergencyServiceNotificationService {

    fun handleEmsNotification(emergencyServiceNotification: EmergencyServiceNotification)

    fun findAll(): Flux<EmergencyServiceNotification>

    fun streamEmsNotifications(): Flux<EmergencyServiceNotification>
}

@Service
class EmergencyServiceNotificationService(private val repository: EmergencyServiceNotificationRepository) : IEmergencyServiceNotificationService {

    private val log = Logger.getLogger(this.javaClass.name)

    private val processor = TopicProcessor.builder<EmergencyServiceNotification>()
            .autoCancel(false)
            .share(true)
            .name("ems_notification_topic")
            .build()

    override fun findAll(): Flux<EmergencyServiceNotification> {
        return repository.findAll()
    }

    override fun streamEmsNotifications(): Flux<EmergencyServiceNotification> {
        return processor
    }

    override fun handleEmsNotification(emergencyServiceNotification: EmergencyServiceNotification) {
        log.info("handling ems notification")
        repository.save(emergencyServiceNotification).subscribe{processor.onNext(it)}
    }

}