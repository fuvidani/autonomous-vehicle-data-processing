package at.ac.tuwien.dse.ss18.group05

import org.junit.Test
import org.springframework.web.reactive.function.client.WebClient

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
class NotificationClientTest {

    private val client = WebClient.create("http://localhost:4000/notifications")

    @Test
    fun testThis() {
        /*client.get()
            .uri("/{id}", "vehicleId")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve().bodyToFlux(String::class.java)
            .take(10)
            .subscribe {
                println("Client A. Message is: $it")
            }

        client.get()
            .uri("/{id}", "vehicleId2")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve().bodyToFlux(String::class.java)
            .take(10)
            .subscribe {
                println("Client B. Message is: $it")
            }

        while (true) {}*/
    }
}