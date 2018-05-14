package at.ac.tuwien.dse.ss18.group05.web

/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.service.IManufacturerNotificationService
import org.springframework.web.bind.annotation.*
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
@RequestMapping("/notifications/manufacturer/{id}")
class ManufacturerNotificationController(private val service: IManufacturerNotificationService) {

    private val pingNotification = ManufacturerNotification(id = "", timeStamp = 0L, accidentId = "", vehicleIdentificationNumber = "", manufacturerId = "", model = "", location = GpsLocation(0.0, 0.0), eventInfo = EventInformation.NONE)

    @GetMapping
    fun getNotifications(@PathVariable("id") vehicleId: String): Flux<ManufacturerNotification> {
        return service
                .streamManufacturerNotifications()
                .startWith(pingNotification)
    }

    @GetMapping("findAllHistoryNotifications")
    fun findAllManufacturerNotifications(@PathVariable("id") manufacturerId: String): Flux<ManufacturerNotification> {
        return service.findAllHistoryNotifications(manufacturerId)
    }
}