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
import java.util.logging.Logger

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

    private val log = Logger.getLogger(this.javaClass.name)

    @GetMapping("/{manufacturerId}/vehicles")
    fun getAllVehiclesOfManufacturer(@PathVariable("manufacturerId") manufacturerId: String): Flux<Vehicle> {
        log.info("getting all vehicles for manufacturer with id: $manufacturerId")
        return vehicleService.findAllVehiclesByManufacturerId(manufacturerId)
    }

    @PostMapping("/{manufacturerId}")
    fun addNewVehicle(@RequestBody vehicle: Vehicle): Mono<Vehicle> {
        log.info("adding new vehicle $vehicle")
        return vehicleService.registerNewVehicle(vehicle)
    }

    @GetMapping("/vehicles")
    fun getAllVehicles(): Flux<Vehicle> {
        log.info("retrieving all vehicles as stream")
        return vehicleService.findAll()
    }

    @ExceptionHandler(ServiceException::class)
    fun serviceExceptionHandler(ex: ServiceException): ResponseEntity<Any> {
        log.warning("catched service exception")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val json = "{\"error\":\"${ex.message}\"}"
        return ResponseEntity(json, headers, HttpStatus.BAD_REQUEST)
    }
}