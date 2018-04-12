package at.ac.tuwien.dse.ss18.group05.notifications

import at.ac.tuwien.dse.ss18.group05.dto.Notification
import com.google.gson.Gson
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.Random
import java.util.UUID

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
class NotificationGenerator(
    private val client: WebClient,
    private val rabbitTemplate: RabbitTemplate,
    private val gson: Gson
) {
    private val random = Random()
    private var counter = 0
    private val vehicles = listOf("firstVehicle", "secondVehicle", "thirdVehicle", "fourthVehicle")

    fun run() {
        vehicles.forEach { vehicle ->
            client.get()
                .uri("notifications/{id}", vehicle)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Notification::class.java)
                .take(10)
                .doOnNext {
                    println("[$vehicle] - Notification received: ${it.message}")
                }
                .doOnComplete { println("Complete") }
                .doOnError { e -> println("Error: $e") }
                .subscribe()
        }
        placeNotificationsPeriodicallyOnEventBus()
    }

    private fun placeNotificationsPeriodicallyOnEventBus() {
        Flux
            .interval(Duration.ofSeconds(2))
            .take(11)
            .subscribe {
                val numberOfVehicles = random.nextInt(5)
                if (numberOfVehicles > 0) {
                    val notification = Notification(
                        UUID.randomUUID().toString(),
                        vehicles.take(numberOfVehicles),
                        "Notification $counter for $numberOfVehicles vehicles"
                    )
                    val json = gson.toJson(notification)
                    rabbitTemplate.convertAndSend("vehicle-data-exchange", "notifications.event", json)
                    counter++
                }
            }
    }
}