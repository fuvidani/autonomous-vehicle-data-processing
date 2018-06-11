package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerDataRecord
import at.ac.tuwien.dse.ss18.group05.dto.toManufacturerDataRecord
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.VehicleDataRecordRepository
import at.ac.tuwien.dse.ss18.group05.service.client.VehicleServiceClient
import org.springframework.stereotype.Component
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
@Component
class TrackerService(
    private val receiver: Receiver,
    private val vehicleServiceClient: VehicleServiceClient,
    private val repository: VehicleDataRecordRepository
) : ITrackerService {

    private val vehicleToManufacturerMap: Map<String, String> by lazy { initializeVehicleToManufacturerMap() }

    /**
     * Returns a hot (i.e. infinite) stream of data records that belong to the
     * manufacturer specified by its ID.
     *
     * @param manufacturerId the unique ID of the manufacturer to use as filter in the stream
     * @return hot Flux of tracking data records
     */
    override fun getTrackingStreamForManufacturer(manufacturerId: String): Flux<ManufacturerDataRecord> {
        return receiver
            .recordStream()
            .map { it.toManufacturerDataRecord() }
            .filter { vehicleToManufacturerMap[it.vehicleIdentificationNumber] == manufacturerId }
    }

    /**
     * Returns all the tracking data records of a manufacturer collected up to this point as a stream, i.e.
     * the history. Consequently, this stream is finite and an onComplete() invocation is guaranteed.
     *
     * @param manufacturerId the unique ID of the manufacturer to use as filter in the stream
     * @return Flux of tracking data records
     */
    override fun getTrackingHistoryForManufacturer(manufacturerId: String): Flux<ManufacturerDataRecord> {
        return repository
            .findAll()
            .map { it.toManufacturerDataRecord() }
            .filter { vehicleToManufacturerMap[it.vehicleIdentificationNumber] == manufacturerId }
    }

    private fun initializeVehicleToManufacturerMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        vehicleServiceClient.getAllVehicles().forEach { result[it.identificationNumber] = it.manufacturerId }
        return result.toMap()
    }
}