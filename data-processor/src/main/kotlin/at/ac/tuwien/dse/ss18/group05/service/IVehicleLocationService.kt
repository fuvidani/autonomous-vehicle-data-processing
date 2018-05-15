package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.VehicleLocation
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import reactor.core.publisher.Mono

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
interface IVehicleLocationService {

    fun save(location: VehicleLocation): Mono<VehicleLocation>

    fun findVehiclesInRadius(point: GpsLocation): Mono<Pair<List<String>, List<String>>>

    fun findVehiclesInRadius(point: GeoJsonPoint): Mono<Pair<List<String>, List<String>>>
}