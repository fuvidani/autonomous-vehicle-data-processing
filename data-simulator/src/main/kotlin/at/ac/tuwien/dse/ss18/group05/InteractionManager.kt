package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.scenario.VehicleSimulator
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
class InteractionManager(private val vehicleSimulator: VehicleSimulator) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val reader = BufferedReader(InputStreamReader(System.`in`))
        var line = ""
        while (!line.equals("q")) {
            line = reader.readLine()
            if (line.contains("crash")) {
                vehicleSimulator.simulateCrash()
            }
        }
    }
}