package at.ac.tuwien.dse.ss18.group05.config

import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
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
    val queueName = "traffic"
    val routingKey = "vehicle.data.#"

    @Bean
    fun queue(): Queue {
        return Queue(queueName, false)
    }

    @Bean
    fun topicExchange(): TopicExchange {
        return TopicExchange(topicExchange)
    }

    @Bean
    fun binding(queue: Queue, topicExchange: TopicExchange): Binding {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey)
    }

    @Bean
    fun container(
        connectionFactory: ConnectionFactory,
        listenerAdapter: MessageListenerAdapter
    ): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer()
        container.connectionFactory = connectionFactory
        container.setQueueNames(queueName)
        container.setMessageListener(listenerAdapter)
        return container
    }

    @Bean
    fun listenerAdapter(receiver: Receiver): MessageListenerAdapter {
        return MessageListenerAdapter(receiver, "receiveMessage")
    }
}