package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.scenario.CarsReader
import at.ac.tuwien.dse.ss18.group05.scenario.RouteReader
import at.ac.tuwien.dse.ss18.group05.scenario.VehicleSimulator
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
class DataSimulatorApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val carsReader = CarsReader("vehicles.yml")
            val routeReader = RouteReader("route_vienna_graz.csv")
            val simulator = VehicleSimulator(carsReader.getVehicles(), routeReader.readRecords())
            simulator.simulate()
        }
    }
}