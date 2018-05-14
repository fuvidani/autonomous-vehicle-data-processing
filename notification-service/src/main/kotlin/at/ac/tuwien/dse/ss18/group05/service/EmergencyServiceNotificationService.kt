package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.repository.EmergencyServiceNotificationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver

interface IEmergencyServiceNotificationService {

    fun findAllHistoryNotifications(): Flux<EmergencyServiceNotification>

    fun streamEmsNotifications(): Flux<EmergencyServiceNotification>
}

@Service
class EmergencyServiceNotificationService(
    private val receiver: Receiver<EmergencyServiceNotification>,
    private val repository: EmergencyServiceNotificationRepository
) : IEmergencyServiceNotificationService {

    override fun findAllHistoryNotifications(): Flux<EmergencyServiceNotification> {
        return repository.findAll()
    }

    override fun streamEmsNotifications(): Flux<EmergencyServiceNotification> {
        return receiver.notificationStream()
    }
}