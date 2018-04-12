package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.Notification
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
class NotificationService(private val receiver: Receiver<Notification>) {

    fun getNotificationForVehicle(id: String): Flux<Notification> {
        return receiver
            .stream()
            .filter { it.concernedVehicles.contains(id) }
    }
}