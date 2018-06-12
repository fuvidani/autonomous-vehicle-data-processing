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

    private val topicExchangeName = "vehicle-data-exchange-2"

    @Bean
    fun vehicleMovementQueue(): Queue {
        return Queue("vehicleQueueProcessing", false)
    }

    @Bean
    fun emsNotificationQueue(): Queue {
        return Queue("emsQueueProcessing", false)
    }

    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange(topicExchangeName)
    }

    @Bean
    fun vehicleBinding(vehicleMovementQueue: Queue, exchange: TopicExchange): Binding {
        return BindingBuilder.bind(vehicleMovementQueue).to(exchange).with("vehicle.data.#")
    }

    @Bean
    fun emsBinding(emsNotificationQueue: Queue, exchange: TopicExchange): Binding {
        return BindingBuilder.bind(emsNotificationQueue).to(exchange).with("ems.notification")
    }
}