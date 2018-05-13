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

    fun getTrackingStreamForManufacturer(manufacturerId: String): Flux<ManufacturerDataRecord>

    fun getTrackingHistoryForManufacturer(manufacturerId: String): Flux<ManufacturerDataRecord>
}