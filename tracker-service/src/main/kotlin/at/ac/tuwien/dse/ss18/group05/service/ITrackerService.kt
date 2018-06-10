package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerDataRecord
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
interface ITrackerService {

    /**
     * Returns a hot (i.e. infinite) stream of data records that belong to the
     * manufacturer specified by its ID.
     *
     * @param manufacturerId the unique ID of the manufacturer to use as filter in the stream
     * @return hot Flux of tracking data records
     */
    fun getTrackingStreamForManufacturer(manufacturerId: String): Flux<ManufacturerDataRecord>

    /**
     * Returns all the tracking data records of a manufacturer collected up to this point as a stream, i.e.
     * the history. Consequently, this stream is finite and an onComplete() invocation is guaranteed.
     *
     * @param manufacturerId the unique ID of the manufacturer to use as filter in the stream
     * @return Flux of tracking data records
     */
    fun getTrackingHistoryForManufacturer(manufacturerId: String): Flux<ManufacturerDataRecord>
}