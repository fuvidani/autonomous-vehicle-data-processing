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

    private val topicExchange = "vehicle-data-exchange"

    @Bean
    fun statisticsQueue(): Queue {
        return Queue("statisticsQueue", false)
    }

    @Bean
    fun topicExchange(): TopicExchange {
        return TopicExchange(topicExchange)
    }

    @Bean
    fun statisticsBinding(statisticsQueue: Queue, topicExchange: TopicExchange): Binding {
        return BindingBuilder.bind(statisticsQueue).to(topicExchange).with("statistics.#")
    }
}