package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.notifications.NotificationGenerator
import com.google.gson.Gson
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.CommandLineRunner

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
@EnableScheduling
class DataSimulatorApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(DataSimulatorApplication::class.java, *args)
        }
    }

    @Bean
    fun client(): WebClient {
        return WebClient.create("http://localhost:4000/")
    }

    @Bean
    fun run(client: WebClient, rabbitTemplate: RabbitTemplate, gson: Gson): CommandLineRunner {
        return CommandLineRunner {
            NotificationGenerator(client, rabbitTemplate, gson).run()
        }
    }
}