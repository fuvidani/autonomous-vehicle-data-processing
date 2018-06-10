package at.ac.tuwien.dse.ss18.group05.config

/* ktlint-disable no-wildcard-imports */
import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
class RabbitConfig {

    private val topicExchange = "vehicle-data-exchange"

    @Bean
    fun vehicleMovementQueue(): Queue {
        return AnonymousQueue()
    }

    @Bean
    fun emsNotificationQueue(): Queue {
        return AnonymousQueue()
    }

    @Bean
    fun direct(): DirectExchange {
        return DirectExchange(topicExchange)
    }

    @Bean
    fun movementBinding(vehicleMovementQueue: Queue, direct: DirectExchange): Binding {
        return BindingBuilder.bind(vehicleMovementQueue).to(direct).with("vehicle-data-movement")
    }

    @Bean
    fun emsNotificationBinding(emsNotificationQueue: Queue, direct: DirectExchange): Binding {
        return BindingBuilder.bind(emsNotificationQueue).to(direct).with("ems-notification")
    }
}