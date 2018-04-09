package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.web.NotificationListener
import org.springframework.stereotype.Component
import kotlin.collections.ArrayList

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
interface Receiver {

    fun receiveMessage(message: String)

    fun registerListener(listener: NotificationListener)

    fun removeListener(listener: NotificationListener)
}

@Component
class NotificationDataReceiver : Receiver {

    override fun removeListener(listener: NotificationListener) {
        listeners.remove(listener)
    }

    private val listeners = ArrayList<NotificationListener>()

    override fun registerListener(listener: NotificationListener) {
        listeners.add(listener)
    }

    override fun receiveMessage(message: String) {
        println("Received <$message>")
        listeners.forEach { it.onNotification(message) }
    }
}