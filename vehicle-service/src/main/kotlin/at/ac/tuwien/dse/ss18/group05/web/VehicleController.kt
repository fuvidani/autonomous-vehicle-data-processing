package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.service.IVehicleService
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import reactor.core.publisher.Flux
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
@RestController
@RequestMapping("/vehicle")
class VehicleController(private val vehicleService: IVehicleService) {

    @GetMapping("/{manufacturerId}/vehicles")
    fun getAllVehiclesOfManufacturer(@PathVariable("manufacturerId") manufacturerId: String): Flux<Vehicle> {
        return vehicleService.findAllVehiclesByManufacturerId(manufacturerId)
    }

    @PostMapping("/{manufacturerId}")
    fun addNewVehicle(@RequestBody vehicle: Vehicle): Mono<Vehicle> {
        return vehicleService.registerNewVehicle(vehicle)
    }
}