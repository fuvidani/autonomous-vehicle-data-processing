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

    /**
     * Notifies the concerned vehicles about the occurrence of a new accident.
     *
     * @param record the source data record which signalled the accident, containing key information about it
     * @param accidentId a unique accident ID identifying this new accident in the system
     * @param concernedVehicles the vehicles that are concerned and should be notified
     */
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

    /**
     * Notifies the concerned vehicles about an update of an already on-going accident. "On-going" means that
     * the accident's occurrence has already been signalled, but is has not been resolved (cleared-up) yet.
     *
     * @param accident the LiveAccident object describing the on-going accident
     * @param emergencyServiceStatus the status of the emergency service
     * @param concernedVehicles the vehicles that are concerned and should be notified
     */
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

    /**
     * Notifies a manufacturer about a near-crash or crash event which happened with their vehicle.
     *
     * @param record the source data record which signalled the (near-)crash event
     * @param accidentId optional accident ID if the record is about a crash event; in case of
     * a near-crash event the accident ID may be null
     * @param manufacturerId ID of the manufacturer the vehicle-record's vehicle belongs to
     */
    override fun notifyManufacturer(record: VehicleDataRecord, accidentId: String?, manufacturerId: String) {
        val notification = record.toManufacturerNotification(manufacturerId, accidentId)
        sender.sendMessage(notification, "notifications-manufacturer")
    }

    /**
     * Notifies the emergency service about a new accident.
     *
     * @param record the source data record which signalled the accident, containing key information about it
     * @param accidentId a unique accident ID identifying this new accident in the system
     */
    override fun notifyEmergencyService(record: VehicleDataRecord, accidentId: String) {
        val notification = record.toEmergencyServiceNotification(accidentId)
        sender.sendMessage(notification, "notifications-ems")
    }

    /**
     * Notifies the emergency service about an update of an already on-going accident. "On-going" means that
     * the accident's occurrence has already been signalled, but is has not been resolved (cleared-up) yet.
     *
     * @param record a dummy emergency service notification with only the accident id and the new status to update
     */
    override fun notifyEMSWithUpdate(record: EmergencyServiceNotification) {
        sender.sendMessage(record, "notifications-ems")
    }

    /**
     * Notifies the statistics service about a closed (i.e. cleared) accident by providing
     * a final report about it.
     *
     * @param accident the LiveAccident object used to gather data during the accident
     */
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