package at.ac.tuwien.dse.ss18.group05.config

import at.ac.tuwien.dse.ss18.group05.dto.RouteRecord
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.scenario.RouteReader
import at.ac.tuwien.dse.ss18.group05.scenario.VehiclesReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.util.logging.Logger

@Configuration
class RootConfig {

    private val log = Logger.getLogger(this.javaClass.name)

    @Bean
    fun client(
        @Value("\${notification.service.host}") host: String,
        @Value("\${notification.service.port}") port: String
    ): WebClient {

        log.info("webclient base url: $host:$port/")
        return WebClient.create("http://$host:$port/")
    }

    @Bean
    fun vehicles(): List<Vehicle> {
        val vehiclesReader = VehiclesReader("vehicles.yml")
        return vehiclesReader.getVehicles()
    }

    @Bean
    fun route(): List<RouteRecord> {
        val routeReader = RouteReader("route_vienna_graz.csv")
        return routeReader.readRecords()
    }
}