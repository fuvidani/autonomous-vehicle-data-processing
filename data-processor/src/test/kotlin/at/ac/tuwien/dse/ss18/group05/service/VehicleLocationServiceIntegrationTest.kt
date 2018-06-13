package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.DataProcessorApplication
import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.VehicleLocation
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
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
class VehicleLocationServiceIntegrationTest {

    @Autowired
    private lateinit var service: IVehicleLocationService
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
    fun testSaveLocationShouldReturnSavedLocation() {
        val location = TestDataProvider.getTestVehicleLocation0()
        StepVerifier.create(service.save(location))
            .expectSubscription()
            .expectNext(location)
            .expectComplete()
            .verify()
    }

    @Test
    fun testUpdateExistingLocationShouldReturnUpdatedLocation() {
        val location = TestDataProvider.getTestVehicleLocation0()
        Assert.assertEquals(location, service.save(location).block())
        val updatedLocation = VehicleLocation(location.vehicleIdentificationNumber, GeoJsonPoint(0.0, 0.0))
        StepVerifier.create(service.save(updatedLocation))
            .expectSubscription()
            .expectNext(updatedLocation)
            .expectComplete()
            .verify()
    }

    @Test
    fun testFindVehiclesInRadiusShouldReturnCorrectListsGpsLocation() {
        (nearVehicleLocations() + farVehicleLocations() + outOfRadiusVehicleLocations()).forEach {
            service.save(it).block()
        }
        // Vienna: 16.3738189, 48.2081743
        val result = service.findVehiclesInRadius(GpsLocation(48.2081743, 16.3738189)).block()!!
        val nearVehiclesList = result.first
        val farVehiclesList = result.second
        Assert.assertNotNull(nearVehiclesList)
        Assert.assertNotNull(farVehiclesList)
        Assert.assertFalse(nearVehiclesList.isEmpty())
        Assert.assertFalse(farVehiclesList.isEmpty())
        Assert.assertEquals(3, nearVehiclesList.size)
        Assert.assertEquals(3, farVehiclesList.size)
        nearVehicleLocations().forEach { Assert.assertTrue(nearVehiclesList.contains(it.vehicleIdentificationNumber)) }
        farVehicleLocations().forEach { Assert.assertTrue(farVehiclesList.contains(it.vehicleIdentificationNumber)) }
        outOfRadiusVehicleLocations().forEach {
            Assert.assertFalse(nearVehiclesList.contains(it.vehicleIdentificationNumber))
            Assert.assertFalse(farVehiclesList.contains(it.vehicleIdentificationNumber))
        }
    }

    @Test
    fun testFindVehiclesInRadiusShouldReturnCorrectListsGeoJsonPoint() {
        (nearVehicleLocations() + farVehicleLocations() + outOfRadiusVehicleLocations()).forEach {
            service.save(it).block()
        }
        // Vienna: 16.3738189, 48.2081743
        val result = service.findVehiclesInRadius(GeoJsonPoint(16.3738189, 48.2081743)).block()!!
        val nearVehiclesList = result.first
        val farVehiclesList = result.second
        Assert.assertNotNull(nearVehiclesList)
        Assert.assertNotNull(farVehiclesList)
        Assert.assertFalse(nearVehiclesList.isEmpty())
        Assert.assertFalse(farVehiclesList.isEmpty())
        Assert.assertEquals(3, nearVehiclesList.size)
        Assert.assertEquals(3, farVehiclesList.size)
        nearVehicleLocations().forEach { Assert.assertTrue(nearVehiclesList.contains(it.vehicleIdentificationNumber)) }
        farVehicleLocations().forEach { Assert.assertTrue(farVehiclesList.contains(it.vehicleIdentificationNumber)) }
        outOfRadiusVehicleLocations().forEach {
            Assert.assertFalse(nearVehiclesList.contains(it.vehicleIdentificationNumber))
            Assert.assertFalse(farVehiclesList.contains(it.vehicleIdentificationNumber))
        }
    }

    @Test
    fun testFindVehiclesInRadiusWithLocationOutOfAreaShouldReturnEmpty() {
        (nearVehicleLocations() + farVehicleLocations() + outOfRadiusVehicleLocations()).forEach {
            service.save(it).block()
        }
        // New York: 43.2994285,-74.2179326
        val result = service.findVehiclesInRadius(GpsLocation(43.2994285, -74.2179326)).block()!!
        val nearVehiclesList = result.first
        val farVehiclesList = result.second
        Assert.assertNotNull(nearVehiclesList)
        Assert.assertNotNull(farVehiclesList)
        Assert.assertTrue(nearVehiclesList.isEmpty())
        Assert.assertTrue(farVehiclesList.isEmpty())
    }

    private fun nearVehicleLocations(): List<VehicleLocation> {
        return listOf(
            VehicleLocation("JH4DB8590SS001561", GeoJsonPoint(16.3735024, 48.20740925)),
            VehicleLocation("3GCPCSE03BG366866", GeoJsonPoint(16.37153902, 48.2080885)),
            VehicleLocation("4T4BE46K19R123050", GeoJsonPoint(16.37663522, 48.20891789))
        )
    }

    private fun farVehicleLocations(): List<VehicleLocation> {
        return listOf(
            VehicleLocation("1G1AS58H497251672", GeoJsonPoint(16.3811467, 48.18872267)),
            VehicleLocation("1FTFW1EFXEFB07248", GeoJsonPoint(16.38449409, 48.18926627)),
            VehicleLocation("4T1BG22K5XU921742", GeoJsonPoint(16.38245561, 48.18840795))
        )
    }

    private fun outOfRadiusVehicleLocations(): List<VehicleLocation> {
        return listOf(
            VehicleLocation("1GNEC13T61R213970", GeoJsonPoint(16.45693445, 47.96329872)),
            VehicleLocation("2HGFG3B5XDH517292", GeoJsonPoint(16.41949093, 47.95301753)),
            VehicleLocation("JTMZD31V086081033", GeoJsonPoint(16.48427081, 47.87499551))
        )
    }
}