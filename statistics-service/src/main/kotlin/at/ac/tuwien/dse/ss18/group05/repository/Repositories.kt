package at.ac.tuwien.dse.ss18.group05.repository

import at.ac.tuwien.dse.ss18.group05.dto.Statistics
import org.springframework.data.mongodb.repository.Tailable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

/**
 * <h4>About this class</h4>

 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
interface StatisticsRepository : ReactiveCrudRepository<Statistics, String> {
    @Tailable
    fun findBy(): Flux<Statistics>
}