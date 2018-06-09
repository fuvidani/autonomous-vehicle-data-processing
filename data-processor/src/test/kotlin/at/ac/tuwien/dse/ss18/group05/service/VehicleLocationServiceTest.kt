package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.VehicleLocation
import at.ac.tuwien.dse.ss18.group05.repository.VehicleLocationRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
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
class VehicleLocationServiceTest {

    @MockBean
    private lateinit var repository: VehicleLocationRepository
    private lateinit var service: IVehicleLocationService

    @Before
    fun setUp() {
        service = VehicleLocationService(repository)
    }

    @Test
    fun testSaveLocationShouldReturnSavedLocation() {
        val location = TestDataProvider.getTestVehicleLocation0()
        Mockito.`when`(repository.save(location)).thenReturn(Mono.just(location))
        StepVerifier.create(service.save(location))
            .expectSubscription()
            .expectNext(location)
            .expectComplete()
            .verify()
    }

    @Test
    fun testFindVehiclesInRadiusShouldReturnCorrectLists() {
        val expectedNearVehiclesList = listOf("JH4DB8590SS001561", "3GCPCSE03BG366866", "4T4BE46K19R123050")
        val expectedFarVehiclesList = listOf("1G1AS58H497251672", "1FTFW1EFXEFB07248", "4T1BG22K5XU921742")

        Mockito.`when`(repository.findByLocationNear(Point(16.3738189, 48.2081743), Distance(1.0, Metrics.KILOMETERS)))
            .thenReturn(
                Flux.fromIterable(listOfTestVehicleLocations())
            )
        Mockito.`when`(repository.findByLocationNear(Point(16.3738189, 48.2081743), Distance(10.0, Metrics.KILOMETERS)))
            .thenReturn(
                Flux.fromIterable(listOfTestVehicleLocations() + dummyListOfVehicleLocations())
            )
        val result = service.findVehiclesInRadius(GpsLocation(48.2081743, 16.3738189)).block()
        Assert.assertNotNull(result)
        Assert.assertEquals(expectedNearVehiclesList, result?.first)
        Assert.assertEquals(expectedFarVehiclesList, result?.second)

        val result2 = service.findVehiclesInRadius(GeoJsonPoint(16.3738189, 48.2081743)).block()
        Assert.assertNotNull(result2)
        Assert.assertEquals(result?.first, result2?.first)
        Assert.assertEquals(result?.second, result2?.second)
    }

    private fun listOfTestVehicleLocations(): List<VehicleLocation> {
        return listOf(
            TestDataProvider.getTestVehicleLocation0(),
            TestDataProvider.getTestVehicleLocation1(),
            TestDataProvider.getTestVehicleLocation2()
        )
    }

    private fun dummyListOfVehicleLocations(): List<VehicleLocation> {
        return listOf(
            VehicleLocation("1G1AS58H497251672", GeoJsonPoint(0.0, 0.0)),
            VehicleLocation("1FTFW1EFXEFB07248", GeoJsonPoint(0.0, 0.0)),
            VehicleLocation("4T1BG22K5XU921742", GeoJsonPoint(0.0, 0.0))
        )
    }
}