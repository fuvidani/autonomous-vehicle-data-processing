package at.ac.tuwien.dse.ss18.group05.messaging

import org.springframework.stereotype.Component

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
}

@Component
class VehicleDataReceiver : Receiver {

    override fun receiveMessage(message: String) {
        println("Received <$message>")
    }
}