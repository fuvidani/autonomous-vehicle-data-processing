package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.dto.Statistics
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

interface IStatisticsService {
    fun findAll(): Flux<Statistics>

    fun create(statistics: Statistics): Mono<Statistics>
}

@Service
class StatisticsService(
        private val statisticsRepository: StatisticsRepository
) : IStatisticsService {
    override fun create(statistics: Statistics): Mono<Statistics> {
        return statisticsRepository.save(statistics)
    }

    override fun findAll(): Flux<Statistics> {
        return statisticsRepository.findAll()
    }

}