package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.Statistics
import at.ac.tuwien.dse.ss18.group05.service.ServiceException
import at.ac.tuwien.dse.ss18.group05.service.IStatisticsService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * <h4>About this class</h4>

 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/statistics")
class StatisticsController(private val statisticsService: IStatisticsService) {

    @GetMapping("/accidents")
    fun getAllStatistics(): Flux<Statistics> {
        return statisticsService.findAll()
    }

    @PostMapping("/accidents")
    fun createStatistics(@RequestBody statistics: Statistics): Mono<Statistics> {
        return statisticsService.create(statistics)
    }

    @ExceptionHandler(ServiceException::class)
    fun serviceExceptionHandler(ex: ServiceException): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val json = "{\"error\":\"${ex.message}\"}"
        return ResponseEntity(json, headers, HttpStatus.BAD_REQUEST)
    }
}