package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.service.IEmergencyServiceNotificationService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
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
@CrossOrigin
@RestController
@RequestMapping("/notifications/ems")
class EmergencyServiceNotificationController(private val service: IEmergencyServiceNotificationService) {

    private val pingNotification = EmergencyServiceNotification(id = "", accidentId = "", timeStamp = 0L, location = GpsLocation(0.0, 0.0), model = "", passengers = 0, status = EmergencyServiceStatus.UNKNOWN)

    @GetMapping
    fun getNotifications(): Flux<EmergencyServiceNotification> {
        return service.streamEmsNotifications().startWith(pingNotification)
    }

    @GetMapping("/findAllHistoryNotifications")
    fun findAllNotifications(): Flux<EmergencyServiceNotification> {
        return service.findAllHistoryNotifications()
    }
}