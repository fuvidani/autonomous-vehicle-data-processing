package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.dto.Notification
import at.ac.tuwien.dse.ss18.group05.repository.NotificationRepository
import com.google.gson.Gson
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
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
interface Receiver<T> {

    fun receiveMessage(message: String)

    fun stream(): Flux<T>
}

@Component
class NotificationDataReceiver(private val notificationRepository: NotificationRepository, private val gson: Gson) :
    Receiver<Notification> {

    private val processor = TopicProcessor.builder<Notification>()
        .autoCancel(false)
        .share(true)
        .name("something")
        .bufferSize(Queues.SMALL_BUFFER_SIZE)
        .build()

    override fun receiveMessage(message: String) {
        println(message)
        val notification = gson.fromJson(message, Notification::class.java)
        notificationRepository.save(notification).subscribe()
        processor.onNext(notification)
    }

    override fun stream(): Flux<Notification> {
        return processor
    }
}