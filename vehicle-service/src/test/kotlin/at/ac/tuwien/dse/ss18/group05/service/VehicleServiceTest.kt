package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.Manufacturer
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.repository.ManufacturerRepository
import at.ac.tuwien.dse.ss18.group05.repository.VehicleRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(SpringRunner::class)
class VehicleServiceTest {

    private lateinit var vehicleService: IVehicleService
    @MockBean
    private lateinit var vehicleRepository: VehicleRepository
    @MockBean
    private lateinit var manufacturerRepository: ManufacturerRepository

    @Before
    fun setUp() {
        Mockito.`when`(vehicleRepository.findAllByManufacturerId("availableManufacturerId"))
            .thenReturn(
                Flux.fromIterable(
                    listOf(
                        Vehicle("identification1", "availableManufacturerId", "Nice model"),
                        Vehicle("identification2", "availableManufacturerId", "Great model"),
                        Vehicle("identification3", "availableManufacturerId", "Bad model")
                    )
                )
            )
        Mockito.`when`(manufacturerRepository.findById("availableManufacturerId"))
            .thenReturn(
                Mono.just(
                    Manufacturer("availableManufacturerId", "Nice manufacturer")
                )
            )
        Mockito.`when`(manufacturerRepository.findById("UnavailableManufacturerId"))
            .thenReturn(Mono.empty())
        Mockito.`when`(vehicleRepository.save(any(Vehicle::class.java)))
            .thenReturn(
                Mono.just(
                    Vehicle("identification4", "availableManufacturerId", "Superb model")
                )
            )
        Mockito.`when`(vehicleRepository.findById("identification4"))
            .thenReturn(Mono.empty())
        Mockito.`when`(vehicleRepository.findById("alreadyStoredVehicleId"))
            .thenReturn(
                Mono.just(
                    Vehicle("alreadyStoredVehicleId", "availableManufacturerId", "Superb model")
                )
            )
        Mockito.`when`(vehicleRepository.findAll()).thenReturn(
            Flux.fromIterable(
                listOf(
                    Vehicle("identification1", "availableManufacturerId1", "Nice model"),
                    Vehicle("identification2", "availableManufacturerId2", "Great model"),
                    Vehicle("identification3", "availableManufacturerId3", "Bad model")
                )
            )
        )
        vehicleService = VehicleService(vehicleRepository, manufacturerRepository)
    }

    @Test
    fun findAllVehiclesByManufacturerShouldReturnCorrectVehicles() {
        val vehicles = vehicleService.findAllVehiclesByManufacturerId("availableManufacturerId")

        StepVerifier.create(vehicles)
            .expectNext(Vehicle("identification1", "availableManufacturerId", "Nice model"))
            .expectNext(Vehicle("identification2", "availableManufacturerId", "Great model"))
            .expectNext(Vehicle("identification3", "availableManufacturerId", "Bad model"))
            .verifyComplete()
    }

    @Test
    fun findAllVehiclesByInvalidManufacturerShouldThrowException() {
        val vehicles = vehicleService.findAllVehiclesByManufacturerId("UnavailableManufacturerId")

        StepVerifier.create(vehicles)
            .expectErrorMessage("Invalid manufacturer")
            .verify()
    }

    @Test
    fun registerNewVehicleShouldReturnPersistedVehicle() {
        val vehicle = Vehicle("identification4", "availableManufacturerId", "Superb model")
        val storedVehicle = vehicleService.registerNewVehicle(vehicle)

        StepVerifier.create(storedVehicle)
            .expectNext(vehicle)
            .verifyComplete()
    }

    @Test
    fun registerAlreadyStoredVehicleShouldThrowException() {
        val vehicle = Vehicle("alreadyStoredVehicleId", "availableManufacturerId", "Superb model")

        StepVerifier.create(vehicleService.registerNewVehicle(vehicle))
            .expectErrorMessage("Vehicle with VIN ${vehicle.identificationNumber} already exists!")
            .verify()
    }

    @Test
    fun findAllShouldReturnAllAvailableVehicles() {
        StepVerifier
            .create(vehicleService.findAll())
            .expectSubscription()
            .expectNext(Vehicle("identification1", "availableManufacturerId1", "Nice model"))
            .expectNext(Vehicle("identification2", "availableManufacturerId2", "Great model"))
            .expectNext(Vehicle("identification3", "availableManufacturerId3", "Bad model"))
            .expectComplete()
            .verify()
    }

    // Kotlin<->Java Mockito type inference workaround
    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}