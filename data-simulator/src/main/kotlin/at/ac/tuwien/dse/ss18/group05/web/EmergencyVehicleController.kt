package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceMessage
import at.ac.tuwien.dse.ss18.group05.notifications.NotificationSender
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class EmergencyVehicleController(private val notificationSender: NotificationSender) {

    @PostMapping("/datasimulation/updatestatus")
    fun updateStatus(@RequestBody(required = true) message: EmergencyServiceMessage): HttpStatus {
        notificationSender.sendEmergencyStatusUpdate(message)
        return HttpStatus.OK
    }
}