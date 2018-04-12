package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Tailable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
@RestController
@RequestMapping("/notifications/{id}")
class NotificationController(
    private val notificationService: NotificationService,
    private val notificationRepository: NotificationRepository
) {

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getNotifications(@PathVariable("id") vehicleId: String): Flux<String> {
        return notificationRepository
            .findBy()
            .filter { it.concernedVehicles.contains(vehicleId) }
            .map {
                it.message
            }
    }
}

@Component
class NotificationService(private val receiver: Receiver) {

    fun getNotificationForVehicle(id: String): Flux<String> {
        return Flux.create { sink ->
            val listener = object : NotificationListener {
                override fun onNotification(message: String) {
                    println("HERE")
                    sink.next(message)
                }
            }
            receiver.registerListener(listener)
            sink.onCancel({
                receiver.removeListener(listener)
            })
        }
    }
}

interface NotificationListener {

    fun onNotification(message: String)
}

@Document(collection = "notifications")
data class Notification(
    @Id
    val id: String,
    val concernedVehicles: List<String>,
    val message: String
)

interface NotificationRepository : ReactiveCrudRepository<Notification, String> {

    @Tailable
    fun findBy(): Flux<Notification>
}