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

    /**
     * Returns a finite stream of vehicles that belong to the manufacturer
     * specified by the manufacturer ID.
     *
     * @param manufacturerId unique ID of the manufacturer
     * @return Flux emitting the vehicles
     * @throws ServiceException if the provided manufacturer ID is invalid
     */
    @Throws(ServiceException::class)
    fun findAllVehiclesByManufacturerId(manufacturerId: String): Flux<Vehicle>

    /**
     * Persists a new vehicle if it is possible.
     *
     * @param vehicle valid Vehicle object to persist
     * @return Mono emitting the persisted Vehicle
     * @throws ServiceException if there is already a vehicle with the same
     * identification number
     */
    @Throws(ServiceException::class)
    fun registerNewVehicle(vehicle: Vehicle): Mono<Vehicle>

    /**
     * Returns a finite stream of all the persisted vehicles.
     *
     * @return Flux emitting vehicle elements
     */
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