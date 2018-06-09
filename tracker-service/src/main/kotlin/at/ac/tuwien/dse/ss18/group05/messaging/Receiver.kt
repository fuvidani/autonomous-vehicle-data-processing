package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import reactor.core.publisher.Flux

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

    /**
     * Receives the provided message.
     *
     * @param message an arbitrary message in String format
     */
    fun receiveMessage(message: String)

    /**
     * Returns a stream of vehicle data records received by this
     * receiver.
     *
     * @return Flux of vehicle data records
     */
    fun recordStream(): Flux<VehicleDataRecord>
}