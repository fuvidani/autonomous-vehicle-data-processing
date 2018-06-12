package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.scenario.VehicleSimulator
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

@CrossOrigin
@RestController
@RequestMapping("/datasimulation")
class VehicleController(private val vehicleSimulator: VehicleSimulator) {

    private val log = Logger.getLogger(this.javaClass.name)

    @PostMapping("/simulatecrash")
    fun simulateCrash(): HttpStatus {
        log.info("simulating crash event")
        vehicleSimulator.simulateEvent(EventInformation.CRASH)
        return HttpStatus.OK
    }

    @PostMapping("/simulatenearcrash")
    fun simulateNearCrash(): HttpStatus {
        log.info("simulating near crash event")
        vehicleSimulator.simulateEvent(EventInformation.NEAR_CRASH)
        return HttpStatus.OK
    }

    @PostMapping("/reset")
    fun resetSimulation(): HttpStatus {
        log.info("request to reset simulation")
        vehicleSimulator.resetSimulation()
        return HttpStatus.OK
    }
}