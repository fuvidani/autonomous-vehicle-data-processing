package at.ac.tuwien.dse.ss18.group05.processing

/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import at.ac.tuwien.dse.ss18.group05.messaging.notification.INotifier
import at.ac.tuwien.dse.ss18.group05.repository.LiveAccidentRepository
import at.ac.tuwien.dse.ss18.group05.service.IVehicleLocationService
import at.ac.tuwien.dse.ss18.group05.service.client.VehicleServiceClient
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
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
@Component
class VehicleDataRecordProcessor(
    vehicleLocationService: IVehicleLocationService,
    accidentRepository: LiveAccidentRepository,
    notifier: INotifier,
    private val vehicleServiceClient: VehicleServiceClient
) : DataProcessor<VehicleDataRecord>(vehicleLocationService, accidentRepository, notifier) {

    private val vehicleToManufacturerMap: Map<String, String> by lazy { initializeVehicleToManufacturerMap() }

    /**
     * Abstract operation which processes the provided data of a generic type.
     * Implementations have the responsibility to know how to process their type
     * of data.
     *
     * @param data the data to process
     */
    override fun process(data: VehicleDataRecord) {
        updateVehicleLocation(data)
        checkAndHandleCrashEvent(data)
    }

    private fun updateVehicleLocation(data: VehicleDataRecord) {
        val location = VehicleLocation(
            data.metaData.identificationNumber,
            GeoJsonPoint(data.sensorInformation.location.lon, data.sensorInformation.location.lat)
        )
        vehicleLocationService.save(location).block()
    }

    private fun checkAndHandleCrashEvent(data: VehicleDataRecord) {
        if (data.eventInformation == EventInformation.NEAR_CRASH) {
            notifyManufacturer(data, null)
        } else if (data.eventInformation == EventInformation.CRASH) {
            handleCrashEvent(data)
        }
    }

    private fun handleCrashEvent(data: VehicleDataRecord) {
        val newAccident = accidentRepository.save(data.toDefaultLiveAccident()).block()
        if (newAccident?.id != null) {
            val vehicles = vehicleLocationService.findVehiclesInRadius(data.sensorInformation.location).block()!!
            notifyManufacturer(data, newAccident.id)
            notifier.notifyEmergencyService(data, newAccident.id)
            notifier.notifyVehiclesOfNewAccident(
                data,
                newAccident.id,
                ConcernedVehicles(vehicles.second, vehicles.first)
            )
        }
    }

    private fun notifyManufacturer(data: VehicleDataRecord, accidentId: String?) {
        val manufacturerId = retrieveManufacturerIdOfVehicle(data)
        notifier.notifyManufacturer(data, accidentId, manufacturerId)
    }

    private fun retrieveManufacturerIdOfVehicle(data: VehicleDataRecord): String {
        val manufacturerId = vehicleToManufacturerMap[data.metaData.identificationNumber]
        if (manufacturerId == null) {
            log.info("There is no cached manufacturer for vehicle with ID (${data.metaData.identificationNumber})")
            return ""
        }
        return manufacturerId
    }

    private fun initializeVehicleToManufacturerMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        vehicleServiceClient.getAllVehicles().forEach { result[it.identificationNumber] = it.manufacturerId }
        log.info("Cached manufacturers: ${result.values.distinct()}")
        return result.toMap()
    }
}