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

    fun notifyVehiclesOfNewAccident(record: VehicleDataRecord, accidentId: String, concernedVehicles: ConcernedVehicles)

    fun notifyVehiclesOfAccidentUpdate(
        accident: LiveAccident,
        emergencyServiceStatus: EmergencyServiceStatus,
        concernedVehicles: ConcernedVehicles
    )

    fun notifyManufacturer(record: VehicleDataRecord, accidentId: String?, manufacturerId: String)

    fun notifyEmergencyService(record: VehicleDataRecord, accidentId: String)

    fun notifyStatisticsService(accident: LiveAccident)
}