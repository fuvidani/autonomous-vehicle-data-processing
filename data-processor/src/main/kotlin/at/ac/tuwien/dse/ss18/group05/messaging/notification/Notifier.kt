package at.ac.tuwien.dse.ss18.group05.messaging.notification

/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import at.ac.tuwien.dse.ss18.group05.messaging.sender.Sender
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
class Notifier(private val sender: Sender) : INotifier {

    override fun notifyVehiclesOfNewAccident(
        record: VehicleDataRecord,
        accidentId: String,
        concernedVehicles: ConcernedVehicles
    ) {
        val notification = record.toVehicleNotification(
            accidentId,
            EmergencyServiceStatus.UNKNOWN,
            concernedVehicles.concernedNearByVehicles,
            concernedVehicles.concernedFarAwayVehicles,
            true,
            10.0
        )
        sender.sendMessage(notification, "notifications-vehicle")
    }

    override fun notifyVehiclesOfAccidentUpdate(
        accident: LiveAccident,
        emergencyServiceStatus: EmergencyServiceStatus,
        concernedVehicles: ConcernedVehicles
    ) {
        val notification = VehicleNotification(
            concernedVehicles.concernedFarAwayVehicles.toTypedArray(),
            concernedVehicles.concernedNearByVehicles.toTypedArray(),
            accident.id!!,
            getMatchingTimestamp(accident, emergencyServiceStatus),
            GpsLocation(accident.location.y, accident.location.x),
            emergencyServiceStatus,
            true,
            10.0
        )
        sender.sendMessage(notification, "notifications-vehicle")
    }

    override fun notifyManufacturer(record: VehicleDataRecord, accidentId: String?, manufacturerId: String) {
        val notification = record.toManufacturerNotification(manufacturerId, accidentId)
        sender.sendMessage(notification, "notifications-manufacturer")
    }

    override fun notifyEmergencyService(record: VehicleDataRecord, accidentId: String) {
        val notification = record.toEmergencyServiceNotification(accidentId)
        sender.sendMessage(notification, "notifications-ems")
    }

    override fun notifyStatisticsService(accident: LiveAccident) {
        val report = accident.toAccidentReport()
        sender.sendMessage(report, "statistics")
    }

    private fun getMatchingTimestamp(accident: LiveAccident, status: EmergencyServiceStatus): Long {
        return if (status == EmergencyServiceStatus.ARRIVED) {
            accident.timestampOfServiceArrival!!
        } else {
            accident.timestampOfSiteClearing!!
        }
    }
}