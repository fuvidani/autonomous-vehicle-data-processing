package at.ac.tuwien.dse.ss18.group05.processing

import at.ac.tuwien.dse.ss18.group05.messaging.notification.INotifier
import at.ac.tuwien.dse.ss18.group05.repository.LiveAccidentRepository
import at.ac.tuwien.dse.ss18.group05.service.IVehicleLocationService
import java.util.logging.Logger

/**
 * <h4>Generic Data Processor</h4>
 *
 * <p>This component encapsulates logic to process some kind of data. Useful dependencies like
 * the IVehicleLocationService, LiveAccidentRepository and INotifier are bundled such that
 * subtypes only need to concentrate how to process their data indicated by the generic type.</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
abstract class DataProcessor<in T>(
    protected val vehicleLocationService: IVehicleLocationService,
    protected val accidentRepository: LiveAccidentRepository,
    protected val notifier: INotifier
) {

    protected val log: Logger = Logger.getLogger(this.javaClass.name)

    /**
     * Abstract operation which processes the provided data of a generic type.
     * Implementations have the responsibility to know how to process their type
     * of data.
     *
     * @param data the data to process
     */
    abstract fun process(data: T)
}