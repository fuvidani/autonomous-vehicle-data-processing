package at.ac.tuwien.dse.ss18.group05.service.client

import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import org.springframework.http.MediaType
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
interface VehicleService {

    @Headers("Accept: ${MediaType.APPLICATION_JSON_UTF8_VALUE}")
    @GET("vehicles")
    fun getAllVehicles(): Call<List<Vehicle>>
}