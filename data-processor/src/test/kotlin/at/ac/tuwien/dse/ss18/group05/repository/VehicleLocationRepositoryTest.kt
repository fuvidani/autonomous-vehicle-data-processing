package at.ac.tuwien.dse.ss18.group05.repository

import at.ac.tuwien.dse.ss18.group05.DataProcessorApplication
import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.dto.VehicleLocation
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeospatialIndex
import org.springframework.test.context.junit4.SpringRunner
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
@SpringBootTest(value = ["application.yml"], classes = [DataProcessorApplication::class])
class VehicleLocationRepositoryTest {

    @Autowired
    private lateinit var repository: VehicleLocationRepository
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin

    @Before
    fun setUp() {
        mongoTemplate.dropCollection(VehicleLocation::class.java)
        mongoTemplate.indexOps(VehicleLocation::class.java)
            .ensureIndex(GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE))
    }

    @Test
    fun testFindAllShouldReturnAllLocations() {
        insertTestVehicleLocations()
        StepVerifier
            .create(repository.findAll())
            .expectSubscription()
            .expectNext(TestDataProvider.getTestVehicleLocation0())
            .expectNext(TestDataProvider.getTestVehicleLocation1())
            .expectNext(TestDataProvider.getTestVehicleLocation2())
            .expectComplete()
            .verify()
    }

    @Test
    fun testGeoSpatialQueryShouldReturnLocationsInRadius() {
        insertTestVehicleLocations()
        StepVerifier
            .create(
                repository.findByLocationNear(
                    Point(16.3755428, 48.2092674), // Figlmueller Baeckerstrasse
                    Distance(1.0, Metrics.KILOMETERS)
                )
            )
            .expectSubscription()
            .expectNext(TestDataProvider.getTestVehicleLocation1())
            .expectNext(TestDataProvider.getTestVehicleLocation0())
            .expectComplete()
            .verify()
    }

    @Test
    fun testGeoSpatialQueryShouldReturnLocationsInRadius2() {
        insertTestVehicleLocations()
        StepVerifier
            .create(
                repository.findByLocationNear(
                    Point(16.3738189, 48.2081743), // Vienna
                    Distance(0.01, Metrics.KILOMETERS)
                )
            )
            .expectSubscription()
            .expectNext(TestDataProvider.getTestVehicleLocation0())
            .expectComplete()
            .verify()
    }

    @Test
    fun testGeoSpatialQueryShouldReturnLocationsInRadius3() {
        insertTestVehicleLocations()
        StepVerifier
            .create(
                repository.findByLocationNear(
                    Point(16.3738189, 48.2081743), // Vienna
                    Distance(10.0, Metrics.KILOMETERS)
                )
            )
            .expectSubscription()
            .expectNext(TestDataProvider.getTestVehicleLocation0())
            .expectNext(TestDataProvider.getTestVehicleLocation1())
            .expectNext(TestDataProvider.getTestVehicleLocation2())
            .expectComplete()
            .verify()
    }

    @Test
    fun testLocationUpdateShouldUpdateLocationCorrectly() {
        insertTestVehicleLocations()
        val location = TestDataProvider.getTestVehicleLocation0()
        val expectedUpdatedLocation = VehicleLocation(
            location.vehicleIdentificationNumber,
            GeoJsonPoint(16.3732133, 48.2089816)
        )
        StepVerifier.create(repository.findById(location.vehicleIdentificationNumber))
            .expectSubscription()
            .expectNext(location)
            .expectComplete()
            .verify()

        StepVerifier.create(repository.save(expectedUpdatedLocation))
            .expectSubscription()
            .expectNext(expectedUpdatedLocation)
            .expectComplete()
            .verify()

        StepVerifier.create(repository.findById(expectedUpdatedLocation.vehicleIdentificationNumber))
            .expectSubscription()
            .expectNext(expectedUpdatedLocation)
            .expectComplete()
            .verify()
    }

    private fun insertTestVehicleLocations() {
        mongoTemplate.insert(TestDataProvider.getTestVehicleLocation0())
        mongoTemplate.insert(TestDataProvider.getTestVehicleLocation1())
        mongoTemplate.insert(TestDataProvider.getTestVehicleLocation2())
    }
}