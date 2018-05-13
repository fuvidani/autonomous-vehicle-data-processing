package at.ac.tuwien.dse.ss18.group05.web

import at.ac.tuwien.dse.ss18.group05.dto.Statistics
import at.ac.tuwien.dse.ss18.group05.service.ServiceException
import at.ac.tuwien.dse.ss18.group05.service.IStatisticsService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.ExceptionHandler
import reactor.core.publisher.Flux

/**
 * <h4>About this class</h4>

 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 1.0.0
 * @since 1.0.0
 */
@CrossOrigin
@RestController
@RequestMapping("/statistics")
class StatisticsController(private val statisticsService: IStatisticsService) {

    @GetMapping("/accidents")
    fun getAllStatistics(): Flux<Statistics> {
        return statisticsService.findBy()
    }

    @ExceptionHandler(ServiceException::class)
    fun serviceExceptionHandler(ex: ServiceException): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val json = "{\"error\":\"${ex.message}\"}"
        return ResponseEntity(json, headers, HttpStatus.BAD_REQUEST)
    }
}