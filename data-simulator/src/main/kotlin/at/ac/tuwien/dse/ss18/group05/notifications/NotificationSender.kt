package at.ac.tuwien.dse.ss18.group05.notifications

import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import org.springframework.stereotype.Component

@Component
class NotificationSender {

    fun sendNotification(notification: VehicleDataRecord) {
        println("this will be sent via rabbit $notification")
    }
}