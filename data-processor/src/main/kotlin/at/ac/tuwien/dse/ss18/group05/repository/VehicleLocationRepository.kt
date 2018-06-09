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

    /**
     * Returns a stream of VehicleLocations that fall into the radius specified by
     * the point and distance parameters.
     *
     * @param point a geo-spatial point value
     * @param distance a distance metric e.g. 1 kilometres, 50 metres...
     * @return Flux stream of location entries matching the radius
     */
    fun findByLocationNear(point: Point, distance: Distance): Flux<VehicleLocation>
}