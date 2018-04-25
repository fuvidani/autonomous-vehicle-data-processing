package at.ac.tuwien.dse.ss18.group05.config

import at.ac.tuwien.dse.ss18.group05.dto.RouteRecord
import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import at.ac.tuwien.dse.ss18.group05.scenario.RouteReader
import at.ac.tuwien.dse.ss18.group05.scenario.VehiclesReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class RootConfig {

    @Bean
    fun client(): WebClient {
        return WebClient.create("http://localhost:4000/")
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