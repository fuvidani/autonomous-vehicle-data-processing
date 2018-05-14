package at.ac.tuwien.dse.ss18.group05.web

/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.service.IVehicleNotificationService
import org.springframework.http.MediaType
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
@RequestMapping("/notifications/vehicle/{id}")
class VehicleNotificationController(private val vehicleNotificationService: IVehicleNotificationService) {

    // Needed as temporary solution because gateway might throw 504 timeout if "nothing" happens on stream
    private val pingNotification = VehicleNotification(id = "", vehicleIdentificationNumber = "", accidentId = "", timestamp = 0L, location = GpsLocation(0.0, 0.0), emergencyServiceStatus = EmergencyServiceStatus.UNKNOWN, specialWarning = null, targetSpeed = null)

    @GetMapping
    fun getNotifications(@PathVariable("id") vehicleId: String): Flux<VehicleNotification> {
        return vehicleNotificationService
                .getNotificationForVehicle(vehicleId)
                .startWith(pingNotification)
    }
}