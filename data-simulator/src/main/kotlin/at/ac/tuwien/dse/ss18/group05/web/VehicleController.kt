package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.scenario.VehicleSimulator
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class VehicleController(private val vehicleSimulator: VehicleSimulator) {

    @PostMapping("/datasimulation/simulatecrash")
    fun simulateCrash(): HttpStatus {
        vehicleSimulator.simulateEvent(EventInformation.CRASH)
        return HttpStatus.OK
    }

    @PostMapping("/datasimulation/simulatenearcrash")
    fun simulateNearCrash(): HttpStatus {
        vehicleSimulator.simulateEvent(EventInformation.NEAR_CRASH)
        return HttpStatus.OK
    }
}