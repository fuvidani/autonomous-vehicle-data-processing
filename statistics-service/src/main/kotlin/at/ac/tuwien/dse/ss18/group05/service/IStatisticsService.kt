package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.AccidentReport
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
interface IStatisticsService {
    fun findAll(): Flux<AccidentReport>

    fun create(accidentReport: AccidentReport): Mono<AccidentReport>
}