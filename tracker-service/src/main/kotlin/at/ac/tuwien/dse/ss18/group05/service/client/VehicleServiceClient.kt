package at.ac.tuwien.dse.ss18.group05.service.client

import at.ac.tuwien.dse.ss18.group05.dto.Vehicle

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
interface VehicleServiceClient {

    fun getAllVehicles(): List<Vehicle>
}