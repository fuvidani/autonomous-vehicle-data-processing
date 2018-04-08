package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
@RestController
@RequestMapping("/vehicle")
class VehicleController {

    private val dummyVehicles: List<Vehicle> = listOf(
        Vehicle("asd", "sedan", 4),
        Vehicle("bla", "cabrio", 2),
        Vehicle("mane", "monster", 25)
    )

    @GetMapping("/vehicles")
    fun getAllVehicles(): Flux<Vehicle> {
        return Flux.fromIterable(dummyVehicles)
    }
}