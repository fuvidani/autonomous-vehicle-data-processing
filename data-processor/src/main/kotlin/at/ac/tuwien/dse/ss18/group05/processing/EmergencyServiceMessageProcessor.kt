package at.ac.tuwien.dse.ss18.group05.processing

/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import at.ac.tuwien.dse.ss18.group05.messaging.notification.INotifier
import at.ac.tuwien.dse.ss18.group05.repository.LiveAccidentRepository
import at.ac.tuwien.dse.ss18.group05.service.IVehicleLocationService
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
class EmergencyServiceMessageProcessor(
    vehicleLocationService: IVehicleLocationService,
    accidentRepository: LiveAccidentRepository,
    notifier: INotifier
) : DataProcessor<EmergencyServiceMessage>(vehicleLocationService, accidentRepository, notifier) {

    /**
     * Abstract operation which processes the provided data of a generic type.
     * Implementations have the responsibility to know how to process their type
     * of data.
     *
     * @param data the data to process
     */
    override fun process(data: EmergencyServiceMessage) {
        if (data.status == EmergencyServiceStatus.ARRIVED) {
            handleArrival(data)
        } else if (data.status == EmergencyServiceStatus.AREA_CLEARED) {
            handleSiteClearing(data)
        }
    }

    private fun handleArrival(data: EmergencyServiceMessage) {
        val currentAccident = accidentRepository.findById(data.accidentId).block()
        if (currentAccident?.id != null) {
            val updatedAccident = accidentRepository.save(currentAccident.withServiceArrival(data.timestamp)).block()!!
            val vehicles = vehicleLocationService.findVehiclesInRadius(updatedAccident.location).block()!!
            notifier.notifyVehiclesOfAccidentUpdate(
                updatedAccident, EmergencyServiceStatus.ARRIVED,
                ConcernedVehicles(vehicles.second, vehicles.first)
            )
        }
    }

    private fun handleSiteClearing(data: EmergencyServiceMessage) {
        val currentAccident = accidentRepository.findById(data.accidentId).block()
        if (currentAccident?.id != null) {
            accidentRepository.delete(currentAccident).block()
            val clearedAccident = currentAccident.withSiteClearing(data.timestamp)
            val vehicles = vehicleLocationService.findVehiclesInRadius(clearedAccident.location).block()!!
            notifier.notifyVehiclesOfAccidentUpdate(
                clearedAccident, EmergencyServiceStatus.AREA_CLEARED,
                ConcernedVehicles(vehicles.second, vehicles.first)
            )
            notifier.notifyStatisticsService(clearedAccident)
        }
    }
}