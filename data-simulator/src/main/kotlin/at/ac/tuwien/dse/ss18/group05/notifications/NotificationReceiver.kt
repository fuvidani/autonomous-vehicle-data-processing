package at.ac.tuwien.dse.ss18.group05.notifications

import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.scenario.VehicleSimulator
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Component
class NotificationReceiver(
    private val client: WebClient,
    private val vehicles: List<Vehicle>,
    private val vehicleSimulator: VehicleSimulator
) {

    private val logger = Logger.getLogger(this.javaClass.name)

    /**
     * opening the connection/stream to the notification service after the object creation
     * receiving all notifications for vehicles here and passing them to the simulator
     */
    @PostConstruct
    fun receive() {
        logger.info("notification receiver instantiated")
        vehicles.forEach { vehicle ->
            client.get()
                    .uri("/notifications/vehicle/{id}", vehicle.identificationNumber)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(VehicleNotification::class.java)
                    //next element which arrives gets passed to simulator
                    .doOnNext { notification ->
                            logger.info("received notification $notification")
                            vehicleSimulator.setSpeedForVehicle(vehicle.identificationNumber, notification.targetSpeed)
                    }
                    .doOnComplete { logger.info("stream completed - disconnected from further notifications") }
                    .doOnError { e -> logger.log(java.util.logging.Level.SEVERE, "error while streaming incoming notifications", e) }
                    .subscribe()
        }
    }
}