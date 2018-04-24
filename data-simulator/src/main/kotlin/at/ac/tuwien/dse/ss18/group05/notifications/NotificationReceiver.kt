package at.ac.tuwien.dse.ss18.group05.notifications

import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration

class NotificationReceiver(
    private val client: WebClient,
    private val vehicles: List<Vehicle>
) {

    fun receive() {
        vehicles.forEach { vehicle ->
            client.get()
                    .uri("notifications/{id}", vehicle)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(VehicleNotification::class.java)
                    .takeUntilOther(Mono.delay(Duration.ofSeconds(60)))
                    .doOnNext {
                        println("[$vehicle] - Notification received: $it")
                    }
                    .doOnComplete { println("Completed") }
                    .doOnError { e -> e.printStackTrace() }
                    .subscribe()
        }
    }
}