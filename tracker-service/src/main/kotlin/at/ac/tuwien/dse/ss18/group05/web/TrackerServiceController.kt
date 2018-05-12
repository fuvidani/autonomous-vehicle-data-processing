package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerDataRecord
import at.ac.tuwien.dse.ss18.group05.service.ITrackerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/tracking")
class TrackerServiceController(private val trackerService: ITrackerService) {

    private val pingManufacturerDataRecord = ManufacturerDataRecord("ping", 0L, "", "", GpsLocation(0.0, 0.0))

    @GetMapping("/manufacturer/{manufacturerId}")
    fun getVehiclesTrackingStream(@PathVariable("manufacturerId") manufacturerId: String): Flux<ManufacturerDataRecord> {
        return trackerService
            .getTrackingStreamForManufacturer(manufacturerId)
            .startWith(pingManufacturerDataRecord)
    }

    @GetMapping("history/manufacturer/{manufacturerId}")
    fun getVehiclesTrackingHistory(@PathVariable("manufacturerId") manufacturerId: String): Flux<ManufacturerDataRecord> {
        return trackerService.getTrackingHistoryForManufacturer(manufacturerId)
    }
}