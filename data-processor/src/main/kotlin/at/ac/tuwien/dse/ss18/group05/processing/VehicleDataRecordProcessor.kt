package at.ac.tuwien.dse.ss18.group05.processing

import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.repository.LiveAccidentRepository
import at.ac.tuwien.dse.ss18.group05.repository.VehicleLocationRepository
import org.springframework.stereotype.Component

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
class VehicleDataRecordProcessor(
    locationRepository: VehicleLocationRepository,
    accidentRepository: LiveAccidentRepository
) : DataProcessor<VehicleDataRecord>(locationRepository, accidentRepository) {

    override fun process(data: VehicleDataRecord) {

    }
}