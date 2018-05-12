package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerDataRecord
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
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

    override fun getTrackingStreamForManufacturer(manufacturerId: String): Flux<ManufacturerDataRecord> {
        return receiver
            .recordStream()
            .map {
                ManufacturerDataRecord(
                    it.id,
                    it.timestamp,
                    it.metaData.identificationNumber,
                    it.metaData.model,
                    it.sensorInformation.location
                )
            }
            .filter { vehicleToManufacturerMap[it.vehicleIdentificationNumber] == manufacturerId }
    }

    override fun getTrackingHistoryForManufacturer(manufacturerId: String): Flux<ManufacturerDataRecord> {
        return repository
            .findAll()
            .map {
                ManufacturerDataRecord(
                    it.id,
                    it.timestamp,
                    it.metaData.identificationNumber,
                    it.metaData.model,
                    it.sensorInformation.location
                )
            }
            .filter { vehicleToManufacturerMap[it.vehicleIdentificationNumber] == manufacturerId }
    }

    private fun initializeVehicleToManufacturerMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        vehicleServiceClient.getAllVehicles().forEach { result[it.identificationNumber] = it.manufacturerId }
        return result.toMap()
    }
}