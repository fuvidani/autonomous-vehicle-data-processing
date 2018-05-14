package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.service.IVehicleNotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableCircuitBreaker
@EnableScheduling
class NotificationServiceApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(NotificationServiceApplication::class.java, *args)
        }
    }
}


@Component
class Something {

    @Autowired
    private lateinit var service: IVehicleNotificationService


    @Scheduled(fixedDelay = 2000)
    fun stream() {
        val incomingVehicleNotification = IncomingVehicleNotification(
                concernedNearByVehicles = arrayOf("id"), concernedFarAwayVehicles = emptyArray(), accidentId = "acc_id", timestamp = 1L, location = GpsLocation(0.0, 0.0), emergencyServiceStatus = EmergencyServiceStatus.UNKNOWN, specialWarning = true, targetSpeed = 30.0
        )

        service.handleIncomingVehicleNotification(incomingVehicleNotification)
        println("scheduled")
    }
}