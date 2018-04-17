package at.ac.tuwien.dse.ss18.group05.repository

import at.ac.tuwien.dse.ss18.group05.dto.Manufacturer
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
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
interface VehicleRepository : ReactiveCrudRepository<Vehicle, String> {

    fun findAllByManufacturerId(manufacturerId: String): Flux<Vehicle>
}

@Repository
interface ManufacturerRepository : ReactiveCrudRepository<Manufacturer, String>