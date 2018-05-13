package at.ac.tuwien.dse.ss18.group05.service.client

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import okhttp3.Request
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner
/* ktlint-disable no-wildcard-imports */
import retrofit2.*

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
class VehicleServiceClientTest {

    private lateinit var vehicleServiceClient: VehicleServiceClient
    private lateinit var vehicleService: VehicleService

    @Before
    fun setUp() {
        vehicleService = Mockito.mock(VehicleService::class.java)
        vehicleServiceClient = VehicleServiceCommand(vehicleService)
    }

    @Test
    fun getAllVehiclesMockAvailableService() {
        val expectedListOfVehicles = listOf(
            TestDataProvider.testVehicleAcura(),
            TestDataProvider.testVehicleAudi(),
            TestDataProvider.testVehicleTesla()
        )
        Mockito.`when`(vehicleService.getAllVehicles()).thenReturn(mockRetrofitWithDesiredList(expectedListOfVehicles))

        Assert.assertEquals(vehicleServiceClient.getAllVehicles(), expectedListOfVehicles)
    }

    @Test
    fun getAllVehiclesTriggerFallback() {
        Mockito.`when`(vehicleService.getAllVehicles()).thenThrow(RuntimeException("Some network issue happened"))
        Assert.assertEquals(vehicleServiceClient.getAllVehicles(), emptyList<Vehicle>())
    }

    private fun mockRetrofitWithDesiredList(vehicles: List<Vehicle>): Call<List<Vehicle>> {
        return object : Call<List<Vehicle>> {
            override fun execute(): Response<List<Vehicle>> {
                return Response.success(vehicles)
            }

            override fun isExecuted(): Boolean {
                return true
            }

            override fun clone(): Call<List<Vehicle>> {
                return this
            }

            override fun isCanceled(): Boolean {
                return true
            }

            override fun cancel() {
                // nothing to do
            }

            override fun request(): Request {
                return this.request()
            }

            override fun enqueue(callback: Callback<List<Vehicle>>?) {
                // nothing to do
            }
        }
    }
}