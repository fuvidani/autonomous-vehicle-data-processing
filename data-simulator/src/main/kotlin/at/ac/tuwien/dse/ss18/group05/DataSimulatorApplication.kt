package at.ac.tuwien.dse.ss18.group05

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener

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
class DataSimulatorApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(DataSimulatorApplication::class.java, *args)
        }
    }

    @Autowired
    private lateinit var rabbitTemplate: RabbitTemplate

    @EventListener(ApplicationReadyEvent::class)
    fun applicationReady() {
        println("Sending message")
        rabbitTemplate.convertAndSend("vehicle-data-exchange", "vehicle.data.bla", "Hello from RabbitMQ!")
    }
}