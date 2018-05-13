package at.ac.tuwien.dse.ss18.group05.processing

/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import at.ac.tuwien.dse.ss18.group05.messaging.sender.Sender
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
    sender: Sender,
    private val vehicleServiceClient: VehicleServiceClient
) : DataProcessor<VehicleDataRecord>(vehicleLocationService, accidentRepository, sender) {

    private val vehicleToManufacturerMap: Map<String, String> by lazy { initializeVehicleToManufacturerMap() }

    override fun process(data: VehicleDataRecord) {
        updateVehicleLocation(data)
        checkAndHandleCrashEvent(data)
    }

    private fun updateVehicleLocation(data: VehicleDataRecord) {
        val location = VehicleLocation(
            data.metaData.identificationNumber,
            GeoJsonPoint(data.sensorInformation.location.lon, data.sensorInformation.location.lat)
        )
        vehicleLocationService.save(location).subscribe()
    }

    private fun checkAndHandleCrashEvent(data: VehicleDataRecord) {
        if (data.eventInformation == EventInformation.NEAR_CRASH) {
            handleNearCrashEvent(data)
        } else if (data.eventInformation == EventInformation.CRASH) {
            handleCrashEvent(data)
        }
    }

    private fun handleCrashEvent(data: VehicleDataRecord) {
        val accident = accidentRepository.save(data.toDefaultLiveAccident()).block()
        if (accident?.id != null) {
            notifyManufacturer(data, accident.id)
            notifyEmergencyService(data, accident.id)
            val vehicles = vehicleLocationService.findVehiclesInRadius(data.sensorInformation.location).block()!!
            notifyVehicles(data, accident.id, vehicles.first, vehicles.second)
        }
    }

    private fun handleNearCrashEvent(data: VehicleDataRecord) {
        notifyManufacturer(data, null)
    }

    private fun notifyManufacturer(data: VehicleDataRecord, accidentId: String?) {
        val manufacturerId = retrieveManufacturerIdOfVehicle(data)
        val notification = data.toManufacturerNotification(manufacturerId, accidentId)
        sender.sendMessage(notification, "notifications.manufacturer")
    }

    private fun notifyEmergencyService(data: VehicleDataRecord, accidentId: String) {
        val notification = data.toEmergencyServiceNotification(accidentId)
        sender.sendMessage(notification, "notifications.ems")
    }

    private fun notifyVehicles(
        data: VehicleDataRecord,
        accidentId: String,
        nearVehiclesList: List<String>,
        farVehiclesList: List<String>
    ) {
        val notification = data.toVehicleNotification(
            accidentId,
            EmergencyServiceStatus.UNKNOWN,
            nearVehiclesList,
            farVehiclesList,
            true,
            10.0
        )
        sender.sendMessage(notification, "notifications.vehicle")
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