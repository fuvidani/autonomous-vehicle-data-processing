package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.repository.ManufacturerRepository
import at.ac.tuwien.dse.ss18.group05.repository.VehicleRepository
import org.springframework.stereotype.Service
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
class ServiceException(message: String) : Exception(message)

interface IVehicleService {

    @Throws(ServiceException::class)
    fun findAllVehiclesByManufacturerId(manufacturerId: String): Flux<Vehicle>

    @Throws(ServiceException::class)
    fun registerNewVehicle(vehicle: Vehicle): Mono<Vehicle>

    fun findAll(): Flux<Vehicle>
}

@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val manufacturerRepository: ManufacturerRepository
) : IVehicleService {

    @Throws(ServiceException::class)
    override fun findAllVehiclesByManufacturerId(manufacturerId: String): Flux<Vehicle> {
        return manufacturerRepository.findById(manufacturerId)
            .switchIfEmpty(Mono.error(ServiceException("Invalid manufacturer")))
            .flatMapMany { _ -> vehicleRepository.findAllByManufacturerId(manufacturerId) }
    }

    @Throws(ServiceException::class)
    override fun registerNewVehicle(vehicle: Vehicle): Mono<Vehicle> {
        return vehicleRepository.findById(vehicle.identificationNumber)
            .flatMap { _ ->
                Mono.error<ServiceException>(
                    ServiceException("Vehicle with VIN ${vehicle.identificationNumber} already exists!")
                )
            }
            .then(vehicleRepository.save(vehicle))
    }

    override fun findAll(): Flux<Vehicle> {
        return vehicleRepository.findAll()
    }
}