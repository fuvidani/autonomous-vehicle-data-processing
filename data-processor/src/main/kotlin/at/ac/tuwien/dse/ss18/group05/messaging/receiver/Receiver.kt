package at.ac.tuwien.dse.ss18.group05.messaging.receiver

import com.google.gson.Gson
import java.util.logging.Logger

/**
 * <h4>Receiver</h4>
 *
 * <p>This component encapsulates the responsibility of receiving messages from the outer
 * world, which can be any type of asynchronous message broker or even another simple
 * component.</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
abstract class Receiver(protected val gson: Gson) {

    protected val log: Logger = Logger.getLogger(this.javaClass.name)

    /**
     * Abstract operation for receiving and presumably handling an incoming arbitrary message.
     *
     * @param message an arbitrary message in String format. Converters might be needed depending on the
     * encoding the message uses.
     */
    abstract fun receiveMessage(message: String)
}