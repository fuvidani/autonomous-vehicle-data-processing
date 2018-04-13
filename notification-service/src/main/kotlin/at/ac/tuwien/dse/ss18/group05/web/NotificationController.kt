package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.Notification
import at.ac.tuwien.dse.ss18.group05.service.NotificationService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable
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
@CrossOrigin
@RestController
@RequestMapping("/notifications/{id}")
class NotificationController(private val notificationService: NotificationService) {

    // Needed as temporary solution because gateway might throw 504 timeout if "nothing" happens on stream
    private val pingNotification = Notification("ping", emptyList(), "ping")

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getNotifications(@PathVariable("id") vehicleId: String): Flux<Notification> {
        return notificationService
            .getNotificationForVehicle(vehicleId)
            .startWith(pingNotification)
//        val notification = Notification("someID", listOf("vehicle1", "vehicle2"), "Surprise mothafucka")
//        return Flux.interval(Duration.ofSeconds(1))
//                .map { notification }
    }
}