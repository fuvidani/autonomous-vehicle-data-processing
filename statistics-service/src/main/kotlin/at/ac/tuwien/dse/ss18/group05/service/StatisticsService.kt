package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.AccidentReport
import at.ac.tuwien.dse.ss18.group05.repository.StatisticsRepository
import org.springframework.stereotype.Service
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
class ServiceException(message: String) : Exception(message)

@Service
class StatisticsService(
    private val statisticsRepository: StatisticsRepository
) : IStatisticsService {
    override fun create(accidentReport: AccidentReport): Mono<AccidentReport> {
        return statisticsRepository.save(accidentReport)
    }

    override fun findAll(): Flux<AccidentReport> {
        return statisticsRepository.findBy()
    }
}