package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.VehicleServiceApplication
import at.ac.tuwien.dse.ss18.group05.dto.Manufacturer
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
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
@SpringBootTest(value = ["application.yml"], classes = [VehicleServiceApplication::class])
class VehicleControllerTest {

    @Rule
    @JvmField
    final val restDocumentation = JUnitRestDocumentation()
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    @Autowired
    private lateinit var vehicleController: VehicleController
    private lateinit var client: WebTestClient

    private val testVehicleBmw1 = Vehicle("WBADE6322VBW51982", "BMW", "1997 BMW 5 Series")
    private val testVehicleBmw2 = Vehicle("1G8MC35B38Y119771", "BMW", "2008 Saturn Sky")
    private val testVehicleBmw3 = Vehicle("3VWSB81H8WM210368", "BMW", "1998 Volkswagen Jetta")
    private val testVehicleAudi1 = Vehicle("TRUSC28N341016582", "Audi", "2004 Audi TT")
    private val testVehicleAudi2 = Vehicle("1B4GP54R2VB378393", "Audi", "1997 Dodge Grand Caravan")
    private val testVehicleCitroen = Vehicle("1GCHK23244F199207", "Citroen", "2004 Chevrolet Silverado 2500HD")

    @Before
    fun setUp() {
        client = WebTestClient.bindToController(vehicleController)
            .configureClient()
            .baseUrl("http://vehicle-service.com/vehicle")
            .filter(documentationConfiguration(restDocumentation))
            .build()
        mongoTemplate.dropCollection(Vehicle::class.java)
        mongoTemplate.dropCollection(Manufacturer::class.java)
    }

    @Test
    fun getAllVehiclesOfUnregisteredManufacturerShouldReturnError() {
        client.get().uri("/{manufacturerId}/vehicles", "unregisteredManufacturer")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .consumeWith {
                Assert.assertEquals("Invalid manufacturer", String(it.responseBody!!))
                document<EntityExchangeResult<ByteArray>>(
                    "manufacturer-vehicles-invalid",
                    pathParameters(
                        parameterWithName("manufacturerId")
                            .description("The ID of the manufacturer")
                    ),
                    responseFields(
                        fieldWithPath("message").description("The error message in case of an invalid request")
                    )
                )
            }
    }

    @Test
    fun getAllVehiclesOfAvailableManufacturerShouldReturnOnlyVehiclesOfThatManufacturer() {
        insertManufacturers()
        insertVehicles()
        client.get().uri("/{manufacturerId}/vehicles", "BMW")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "manufacturer-vehicles-valid",
                    pathParameters(
                        parameterWithName("manufacturerId")
                            .description("The ID of the manufacturer")
                    ),
                    responseFields(
                        fieldWithPath("identificationNumber")
                            .description("The vehicle's world-wide unique Vehicle Identification Number"),
                        fieldWithPath("manufacturerId")
                            .description("ID of the manufacturer"),
                        fieldWithPath("model")
                            .description("The vehicle's type of model")
                    )
                )
            )
        val stream = client.get().uri("/BMW/vehicles")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(Vehicle::class.java)

        StepVerifier.create(stream.responseBody)
            .expectNext(testVehicleBmw1)
            .expectNext(testVehicleBmw2)
            .expectNext(testVehicleBmw3)
            .verifyComplete()
    }

    @Test
    fun getAllVehiclesOfAvailableManufacturerShouldReturnOnlyVehiclesOfThatManufacturer2() {
        insertManufacturers()
        insertVehicles()
        val stream = client.get().uri("/{manufacturerId}/vehicles", "Audi")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(Vehicle::class.java)

        StepVerifier.create(stream.responseBody)
            .expectNext(testVehicleAudi1)
            .expectNext(testVehicleAudi2)
            .verifyComplete()
    }

    @Test
    fun getAllVehiclesOfAvailableManufacturerShouldReturnOnlyVehiclesOfThatManufacturer3() {
        insertManufacturers()
        insertVehicles()
        val stream = client.get().uri("/{manufacturerId}/vehicles", "Citroen")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(Vehicle::class.java)

        StepVerifier.create(stream.responseBody)
            .expectNext(testVehicleCitroen)
            .verifyComplete()
    }

    @Test
    fun addNewVehicleShouldPersist() {
        insertManufacturers()
        insertVehicles()

        val newVehicle = Vehicle("1G8ZG127XWZ157259", "BMW", "1998 Saturn S Series")
        client.post().uri("/{manufacturerId}", "BMW")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(newVehicle), Vehicle::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.identificationNumber").isEqualTo(newVehicle.identificationNumber)
            .jsonPath("$.manufacturerId").isEqualTo(newVehicle.manufacturerId)
            .jsonPath("$.model").isEqualTo(newVehicle.model)
            .consumeWith(
                document(
                    "add-new-vehicle-valid",
                    pathParameters(
                        parameterWithName("manufacturerId")
                            .description("The ID of the manufacturer")
                    ),
                    requestFields(
                        fieldWithPath("identificationNumber")
                            .description("The vehicle's world-wide unique Vehicle Identification Number"),
                        fieldWithPath("manufacturerId")
                            .description("ID of the manufacturer"),
                        fieldWithPath("model")
                            .description("The vehicle's type of model")
                    ),
                    responseFields(
                        fieldWithPath("identificationNumber")
                            .description("The vehicle's world-wide unique Vehicle Identification Number"),
                        fieldWithPath("manufacturerId")
                            .description("ID of the manufacturer"),
                        fieldWithPath("model")
                            .description("The vehicle's type of model")
                    )
                )
            )
    }

    @Test
    fun addAlreadyExistingVehicleShouldReturnError() {
        insertManufacturers()
        insertVehicles()
        client.post().uri("/{manufacturerId}", "Citroen")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(testVehicleCitroen), Vehicle::class.java)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .consumeWith {
                Assert.assertEquals(
                    "Vehicle with VIN ${testVehicleCitroen.identificationNumber} already exists!",
                    String(it.responseBody!!)
                )
                document<EntityExchangeResult<ByteArray>>(
                    "add-new-vehicle-invalid",
                    pathParameters(
                        parameterWithName("manufacturerId")
                            .description("The ID of the manufacturer")
                    ),
                    requestFields(
                        fieldWithPath("identificationNumber")
                            .description("The vehicle's world-wide unique Vehicle Identification Number"),
                        fieldWithPath("manufacturerId")
                            .description("ID of the manufacturer"),
                        fieldWithPath("model")
                            .description("The vehicle's type of model")
                    ),
                    responseFields(
                        fieldWithPath("message").description("The error message in case of an invalid request")
                    )
                )
            }
    }

    private fun insertManufacturers() {
        mongoTemplate.insert(Manufacturer("Audi", "Audi"))
        mongoTemplate.insert(Manufacturer("BMW", "BMW"))
        mongoTemplate.insert(Manufacturer("Citroen", "Citroen"))
    }

    private fun insertVehicles() {
        mongoTemplate.insert(testVehicleBmw1)
        mongoTemplate.insert(testVehicleBmw2)
        mongoTemplate.insert(testVehicleBmw3)
        mongoTemplate.insert(testVehicleAudi1)
        mongoTemplate.insert(testVehicleAudi2)
        mongoTemplate.insert(testVehicleCitroen)
    }
}