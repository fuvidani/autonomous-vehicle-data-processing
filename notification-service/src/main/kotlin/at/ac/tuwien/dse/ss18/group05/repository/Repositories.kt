package at.ac.tuwien.dse.ss18.group05.repository

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
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

@Repository
interface VehicleNotificationRepository : ReactiveCrudRepository<VehicleNotification, String> {
    fun findByVehicleIdentificationNumber(id: String): Flux<VehicleNotification>
}

@Repository
interface EmergencyServiceNotificationRepository : ReactiveCrudRepository<EmergencyServiceNotification, String>

@Repository
interface ManufacturerNotificationRepository : ReactiveCrudRepository<ManufacturerNotification, String> {

    fun findByManufacturerId(id: String): Flux<ManufacturerNotification>
}
