package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceMessage
import at.ac.tuwien.dse.ss18.group05.notifications.VehicleDataSender
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

@CrossOrigin
@RestController
class EmergencyVehicleController(private val vehicleDataSender: VehicleDataSender) {

    private val log = Logger.getLogger(this.javaClass.name)

    @PostMapping("/datasimulation/updatestatus")
    fun updateStatus(@RequestBody(required = true) message: EmergencyServiceMessage): HttpStatus {
        log.info("received status update $message")
        vehicleDataSender.sendEmergencyStatusUpdate(message)
        return HttpStatus.OK
    }
}