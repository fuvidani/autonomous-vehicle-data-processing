package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.scenario.VehicleSimulator
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InteractionManager(private val vehicleSimulator: VehicleSimulator) {

    @Scheduled(fixedDelay = 2500)
    fun run() {
        vehicleSimulator.simulateCrash()
    }
}