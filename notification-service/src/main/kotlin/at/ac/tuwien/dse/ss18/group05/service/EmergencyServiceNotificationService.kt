package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.repository.EmergencyServiceNotificationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import java.util.logging.Logger

interface IEmergencyServiceNotificationService {

    fun findAllHistoryNotifications(): Flux<EmergencyServiceNotification>

    fun streamEmsNotifications(): Flux<EmergencyServiceNotification>
}

@Service
class EmergencyServiceNotificationService(
    private val receiver: Receiver<EmergencyServiceNotification>,
    private val repository: EmergencyServiceNotificationRepository
) : IEmergencyServiceNotificationService {

    private val log = Logger.getLogger(this.javaClass.name)

    override fun findAllHistoryNotifications(): Flux<EmergencyServiceNotification> {
        log.info("returning all history notifications for EMS")
        return repository.findAll()
    }

    override fun streamEmsNotifications(): Flux<EmergencyServiceNotification> {
        return receiver.notificationStream()
    }
}