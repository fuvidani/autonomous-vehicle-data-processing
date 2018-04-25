package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.notifications.NotificationSender
import at.ac.tuwien.dse.ss18.group05.scenario.CarsReader
import at.ac.tuwien.dse.ss18.group05.scenario.RouteReader
import at.ac.tuwien.dse.ss18.group05.scenario.VehicleSimulator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class InteractionManager : CommandLineRunner {

    @Autowired
    private lateinit var notificationSender: NotificationSender

    override fun run(vararg args: String?) {

        val carsReader = CarsReader("vehicles.yml")
        val routeReader = RouteReader("route_vienna_graz.csv")
        val simulator = VehicleSimulator(carsReader.getVehicles(), routeReader.readRecords(), notificationSender)
        simulator.simulate()
    }
}