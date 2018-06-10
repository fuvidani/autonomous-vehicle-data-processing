package at.ac.tuwien.dse.ss18.group05.messaging.notification

import at.ac.tuwien.dse.ss18.group05.dto.ConcernedVehicles
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceStatus
import at.ac.tuwien.dse.ss18.group05.dto.LiveAccident
import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
interface INotifier {

    /**
     * Notifies the concerned vehicles about the occurrence of a new accident.
     *
     * @param record the source data record which signalled the accident, containing key information about it
     * @param accidentId a unique accident ID identifying this new accident in the system
     * @param concernedVehicles the vehicles that are concerned and should be notified
     */
    fun notifyVehiclesOfNewAccident(record: VehicleDataRecord, accidentId: String, concernedVehicles: ConcernedVehicles)

    /**
     * Notifies the concerned vehicles about an update of an already on-going accident. "On-going" means that
     * the accident's occurrence has already been signalled, but is has not been resolved (cleared-up) yet.
     *
     * @param accident the LiveAccident object describing the on-going accident
     * @param emergencyServiceStatus the status of the emergency service
     * @param concernedVehicles the vehicles that are concerned and should be notified
     */
    fun notifyVehiclesOfAccidentUpdate(
        accident: LiveAccident,
        emergencyServiceStatus: EmergencyServiceStatus,
        concernedVehicles: ConcernedVehicles
    )

    /**
     * Notifies a manufacturer about a near-crash or crash event which happened with their vehicle.
     *
     * @param record the source data record which signalled the (near-)crash event
     * @param accidentId optional accident ID if the record is about a crash event; in case of
     * a near-crash event the accident ID may be null
     * @param manufacturerId ID of the manufacturer the vehicle-record's vehicle belongs to
     */
    fun notifyManufacturer(record: VehicleDataRecord, accidentId: String?, manufacturerId: String)

    /**
     * Notifies the emergency service about a new accident.
     *
     * @param record the source data record which signalled the accident, containing key information about it
     * @param accidentId a unique accident ID identifying this new accident in the system
     */
    fun notifyEmergencyService(record: VehicleDataRecord, accidentId: String)

    /**
     * Notifies the statistics service about a closed (i.e. cleared) accident by providing
     * a final report about it.
     *
     * @param accident the LiveAccident object used to gather data during the accident
     */
    fun notifyStatisticsService(accident: LiveAccident)
}