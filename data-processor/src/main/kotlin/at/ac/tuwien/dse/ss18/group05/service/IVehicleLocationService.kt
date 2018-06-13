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

    /**
     * Saves (i.e. inserts or updates) the given vehicle location to the
     * repository.
     *
     * @param location the vehicle location to save
     * @return Mono emitting the saved vehicle location
     */
    fun save(location: VehicleLocation): Mono<VehicleLocation>

    /**
     * Finds vehicles by their IDs for which the last known location lies within a 10
     * kilometres radius. This list is furthermore split up into two parts, where the first
     * contains the vehicles within 1 kilometer ("near") and the second the rest of them ("far").
     *
     * @param point a valid GPS location (lat, lon)
     * @return Mono emitting a pair object of lists; The first list contains the near vehicle IDs, while
     * the second list contains the far vehicle IDs
     */
    fun findVehiclesInRadius(point: GpsLocation): Mono<Pair<List<String>, List<String>>>

    /**
     * Finds vehicles by their IDs for which the last known location lies within a 10
     * kilometres radius. This list is furthermore split up into two parts, where the first
     * contains the vehicles within 1 kilometer ("near") and the second the rest of them ("far").
     *
     * @param point a valid GeoJsonPoint (x, y)
     * @return Mono emitting a pair object of lists; The first list contains the near vehicle IDs, while
     * the second list contains the far vehicle IDs
     */
    fun findVehiclesInRadius(point: GeoJsonPoint): Mono<Pair<List<String>, List<String>>>
}