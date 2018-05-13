package at.ac.tuwien.dse.ss18.group05.processing

/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import at.ac.tuwien.dse.ss18.group05.messaging.sender.Sender
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
    sender: Sender
) : DataProcessor<EmergencyServiceMessage>(vehicleLocationService, accidentRepository, sender) {

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
            val accident = accidentRepository.save(currentAccident.withServiceArrival(data.timestamp)).block()!!
            val vehicles = vehicleLocationService.findVehiclesInRadius(accident.location).block()!!
            notifyVehicles(accident, data.timestamp, EmergencyServiceStatus.ARRIVED, vehicles.first, vehicles.second)
        }
    }

    private fun handleSiteClearing(data: EmergencyServiceMessage) {
        val currentAccident = accidentRepository.findById(data.accidentId).block()
        if (currentAccident?.id != null) {
            accidentRepository.delete(currentAccident).subscribe()
            val accident = currentAccident.withSiteClearing(data.timestamp)
            val vehicles = vehicleLocationService.findVehiclesInRadius(accident.location).block()!!
            notifyVehicles(
                accident,
                data.timestamp,
                EmergencyServiceStatus.AREA_CLEARED,
                vehicles.first,
                vehicles.second
            )
            notifyStatisticsService(accident)
        }
    }

    private fun notifyVehicles(
        accident: LiveAccident,
        timestamp: Long,
        emergencyServiceStatus: EmergencyServiceStatus,
        nearVehiclesList: List<String>,
        farVehiclesList: List<String>
    ) {
        val notification = VehicleNotification(
            farVehiclesList.toTypedArray(),
            nearVehiclesList.toTypedArray(),
            accident.id!!,
            timestamp,
            GpsLocation(accident.location.y, accident.location.x),
            emergencyServiceStatus,
            true,
            10.0
        )
        sender.sendMessage(notification, "notifications.vehicle")
    }

    private fun notifyStatisticsService(accident: LiveAccident) {
        val report = accident.toAccidentReport()
        sender.sendMessage(report, "statistics.report")
    }
}