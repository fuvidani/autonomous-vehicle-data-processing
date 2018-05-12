package at.ac.tuwien.dse.ss18.group05.service.client

import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandGroupKey
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
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
@Component
class VehicleServiceCommand(private val vehicleService: VehicleService) :
    HystrixCommand<List<Vehicle>>(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("VehicleServiceGroup"))),
    VehicleServiceClient {

    private val log = Logger.getLogger(this.javaClass.name)

    override fun getAllVehicles(): List<Vehicle> {
        return this.execute()
    }

    override fun run(): List<Vehicle> {
        return vehicleService.getAllVehicles().execute().body() ?: throw RuntimeException("Vehicle Service result null")
    }

    override fun getFallback(): List<Vehicle> {
        log.info("Vehicle-Service not reachable, returning fallback")
        return listOf(Vehicle("JH4DB8590SS001561", "Acura", "1995 Acura Integra"))
    }
}