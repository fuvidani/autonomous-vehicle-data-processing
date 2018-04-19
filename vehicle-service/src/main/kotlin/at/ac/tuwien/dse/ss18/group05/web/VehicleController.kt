package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.service.IVehicleService
import at.ac.tuwien.dse.ss18.group05.service.ServiceException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
/* ktlint-disable no-wildcard-imports */
import org.springframework.web.bind.annotation.*
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

    @ExceptionHandler(ServiceException::class)
    fun serviceExceptionHandler(ex: ServiceException): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val json = "{\"error\":\"${ex.message}\"}"
        return ResponseEntity(json, headers, HttpStatus.BAD_REQUEST)
    }
}