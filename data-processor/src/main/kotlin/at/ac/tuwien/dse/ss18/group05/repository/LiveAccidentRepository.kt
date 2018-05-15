package at.ac.tuwien.dse.ss18.group05.repository

import at.ac.tuwien.dse.ss18.group05.dto.LiveAccident
import org.springframework.data.repository.reactive.ReactiveCrudRepository

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
interface LiveAccidentRepository : ReactiveCrudRepository<LiveAccident, String>