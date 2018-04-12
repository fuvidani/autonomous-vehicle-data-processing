package at.ac.tuwien.dse.ss18.group05.repository

import at.ac.tuwien.dse.ss18.group05.dto.Notification
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
interface NotificationRepository : ReactiveCrudRepository<Notification, String> {

    /*
    * Uncomment this if you'd like to experiment with full reactive capped collection
    @Tailable
    fun findBy(): Flux<Notification>
    */
}