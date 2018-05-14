package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.dto.*
import at.ac.tuwien.dse.ss18.group05.service.IVehicleNotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.TopicProcessor
import reactor.util.concurrent.Queues

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

    @Bean
    fun vehicleDataRecordStreamProcessor(): TopicProcessor<VehicleNotification> {
        return TopicProcessor.builder<VehicleNotification>()
                .autoCancel(false)
                .share(true)
                .name("something")
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .build()
    }
}

