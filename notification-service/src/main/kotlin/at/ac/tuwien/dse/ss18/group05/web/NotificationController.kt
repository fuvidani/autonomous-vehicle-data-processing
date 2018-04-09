package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
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
class NotificationController(private val notificationService: NotificationService) {

    @GetMapping
    fun getNotifications(@PathVariable("id") vehicleId: String): Flux<String> {
        /*return Flux.interval(Duration.ofSeconds(1))
            .map { _ ->  "Notification for vehicle $vehicleId"}*/
        return notificationService.getNotificationForVehicle(vehicleId)
    }
}

@Component
class NotificationService(private val receiver: Receiver) {

    fun getNotificationForVehicle(id: String): Flux<String> {
        return Flux.create {
            val listener = object : NotificationListener {
                override fun onNotification(message: String) {
                    println("HERE")
                    it.next(message)
                }
            }
            receiver.registerListener(listener)
        }
    }
}

interface NotificationListener {

    fun onNotification(message: String)
}