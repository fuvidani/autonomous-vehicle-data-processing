package at.ac.tuwien.dse.ss18.group05.messaging

import com.google.gson.Gson
import java.util.logging.Logger


/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
abstract class Receiver(protected val gson: Gson) {

    protected val log: Logger = Logger.getLogger(this.javaClass.name)

    abstract fun receiveMessage(message: String)
}