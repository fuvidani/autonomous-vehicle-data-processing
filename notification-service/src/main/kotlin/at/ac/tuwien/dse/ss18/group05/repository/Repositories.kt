package at.ac.tuwien.dse.ss18.group05.repository

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface VehicleNotificationRepository : ReactiveCrudRepository<VehicleNotification, String> {

    /**
     * retrieving all information to a specific vehicle with the given identification number
     *
     * @param id the identification number of the vehicle to find all information
     */
    fun findByVehicleIdentificationNumber(id: String): Flux<VehicleNotification>
}

@Repository
interface EmergencyServiceNotificationRepository : ReactiveCrudRepository<EmergencyServiceNotification, String> {

    /**
     * searching for emergency service notifications with a given accident id
     *
     * @param id the accident id of the notification
     */
    fun findByAccidentId(id: String): Mono<EmergencyServiceNotification>
}

@Repository
interface ManufacturerNotificationRepository : ReactiveCrudRepository<ManufacturerNotification, String> {

    /**
     * searching all notifications for a given manufacturer - identified by it's id
     *
     * @param id the id of the manufcturer to search
     */
    fun findByManufacturerId(id: String): Flux<ManufacturerNotification>
}
