package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.VehicleLocation
import at.ac.tuwien.dse.ss18.group05.repository.VehicleLocationRepository
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.stereotype.Component
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
@Component
class VehicleLocationService(private val repository: VehicleLocationRepository) : IVehicleLocationService {

    override fun save(location: VehicleLocation): Mono<VehicleLocation> {
        return repository.save(location)
    }

    override fun findVehiclesInRadius(point: GpsLocation): Mono<Pair<List<String>, List<String>>> {
        return findVehiclesInRadius(Point(point.lon, point.lat))
    }

    override fun findVehiclesInRadius(point: GeoJsonPoint): Mono<Pair<List<String>, List<String>>> {
        return findVehiclesInRadius(Point(point.x, point.y))
    }

    private fun findVehiclesInRadius(point: Point): Mono<Pair<List<String>, List<String>>> {
        val nearVehicles =
            repository.findByLocationNear(point, Distance(1.0, Metrics.KILOMETERS)).collectList().block()!!
                .map { it.vehicleIdentificationNumber }
        val farVehicles =
            repository.findByLocationNear(point, Distance(10.0, Metrics.KILOMETERS)).collectList().block()!!
                .map { it.vehicleIdentificationNumber }
                .filter { !nearVehicles.contains(it) }
        return Mono.just(Pair(nearVehicles, farVehicles))
    }
}