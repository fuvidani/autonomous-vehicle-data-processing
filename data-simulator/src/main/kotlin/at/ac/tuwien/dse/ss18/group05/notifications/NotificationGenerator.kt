package at.ac.tuwien.dse.ss18.group05.notifications

import at.ac.tuwien.dse.ss18.group05.dto.Notification
import com.google.gson.Gson
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
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
    private val vehicles = listOf(
        "firstVehicle",
        "secondVehicle",
        "thirdVehicle",
        "fourthVehicle",
        "firstVehicle1",
        "firstVehicle2",
        "firstVehicle3",
        "firstVehicle4",
        "firstVehicle5",
        "firstVehicle6",
        "firstVehicle7",
        "firstVehicle8",
        "firstVehicle9",
        "firstVehicle10"
    )

    fun run() {
        vehicles.forEach { vehicle ->
            Thread.sleep(500)
            client.get()
                .uri("notifications/{id}", vehicle)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Notification::class.java)
                .takeUntilOther(Mono.delay(Duration.ofSeconds(60)))
                .doOnNext {
                    println("[$vehicle] - Notification received: ${it.message}")
                }
                .doOnComplete { println("Completed") }
                .doOnError { e -> e.printStackTrace() }
                .subscribe()
        }
        Thread.sleep(2000)
        println("------------------------------------- START EMITTING NOTIFICATIONS -------------------------------------")
        placeNotificationsPeriodicallyOnEventBus(2, 40)
    }

    private fun placeNotificationsPeriodicallyOnEventBus(intervalInSeconds: Long, durationInSeconds: Long) {
        Flux
            .interval(Duration.ofSeconds(intervalInSeconds))
            .takeUntilOther(Mono.delay(Duration.ofSeconds(durationInSeconds)))
            .doOnNext {
                val numberOfVehicles = random.nextInt(vehicles.size + 1)
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
            .doOnComplete {
                println("------------------------------------- STOP EMITTING NOTIFICATIONS -------------------------------------")
                println("--------------------------- ! WAIT FOR ${vehicles.size} CLIENTS TO COMPLETE ! -------------------------")
            }
            .doOnError { e -> e.printStackTrace() }
            .subscribe()
    }
}