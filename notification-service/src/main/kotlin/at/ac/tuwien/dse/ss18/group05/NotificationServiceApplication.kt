package at.ac.tuwien.dse.ss18.group05
/* ktlint-disable no-wildcard-imports */

import at.ac.tuwien.dse.ss18.group05.dto.*
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.context.annotation.Bean
import org.springframework.http.CacheControl
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
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
class NotificationServiceApplication : WebFluxConfigurer {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(NotificationServiceApplication::class.java, *args)
        }
    }

    @Bean
    fun vehicleNotificationProcessor(): TopicProcessor<VehicleNotification> {
        return TopicProcessor.builder<VehicleNotification>()
                .autoCancel(false)
                .share(true)
                .name("vehicle_notification_processor")
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .build()
    }

    @Bean
    fun emsNotificationProcessor(): TopicProcessor<EmergencyServiceNotification> {
        return TopicProcessor.builder<EmergencyServiceNotification>()
                .autoCancel(false)
                .share(true)
                .name("ems_notification_processor")
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .build()
    }

    @Bean
    fun manufacturerNotificationProcessor(): TopicProcessor<ManufacturerNotification> {
        return TopicProcessor.builder<ManufacturerNotification>()
                .autoCancel(false)
                .share(true)
                .name("manufacturer_notification_processor")
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .build()
    }

    /**
     * Add resource handlers for serving static resources.
     * @see ResourceHandlerRegistry
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/notifications/resources/**")
            .addResourceLocations("classpath:/static/docs/")
            .setCacheControl(CacheControl.noStore())
    }
}
