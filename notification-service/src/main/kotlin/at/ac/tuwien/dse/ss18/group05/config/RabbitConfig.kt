package at.ac.tuwien.dse.ss18.group05.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
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

    val topicExchange = "vehicle-data-exchange"

    @Bean
    fun vehicleQueue(): Queue {
        return Queue("vehicleQueue", false)
    }

    @Bean
    fun emsQueue(): Queue {
        return Queue("emsQueue", false)
    }

    @Bean
    fun manufacturerQueue(): Queue {
        return Queue("manufacturerQueue", false)
    }

    @Bean
    fun topicExchange(): TopicExchange {
        return TopicExchange(topicExchange)
    }

    @Bean
    fun vehicleBinding(vehicleQueue: Queue, topicExchange: TopicExchange): Binding {
        return BindingBuilder.bind(vehicleQueue).to(topicExchange).with("notifications.vehicle")
    }

    @Bean
    fun emsBinding(emsQueue: Queue, topicExchange: TopicExchange): Binding {
        return BindingBuilder.bind(emsQueue).to(topicExchange).with("notifications.ems")
    }

    @Bean
    fun manufacturerBinding(manufacturerQueue: Queue, topicExchange: TopicExchange): Binding {
        return BindingBuilder.bind(manufacturerQueue).to(topicExchange).with("notifications.manufacturer")
    }
}