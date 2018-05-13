package at.ac.tuwien.dse.ss18.group05.processing

import at.ac.tuwien.dse.ss18.group05.messaging.sender.Sender
import at.ac.tuwien.dse.ss18.group05.repository.LiveAccidentRepository
import at.ac.tuwien.dse.ss18.group05.repository.VehicleLocationRepository
import java.util.logging.Logger

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
abstract class DataProcessor<in T>(
    protected val vehicleLocationRepository: VehicleLocationRepository,
    protected val accidentRepository: LiveAccidentRepository,
    protected val sender: Sender
) {

    protected val log: Logger = Logger.getLogger(this.javaClass.name)

    abstract fun process(data: T)
}