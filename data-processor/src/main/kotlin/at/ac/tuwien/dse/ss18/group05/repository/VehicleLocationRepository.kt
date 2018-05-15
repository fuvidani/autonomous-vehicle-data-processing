package at.ac.tuwien.dse.ss18.group05.repository

import at.ac.tuwien.dse.ss18.group05.dto.VehicleLocation
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.repository.reactive.ReactiveCrudRepository
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
interface VehicleLocationRepository : ReactiveCrudRepository<VehicleLocation, String> {

    fun findByLocationNear(point: Point, distance: Distance): Flux<VehicleLocation>
}