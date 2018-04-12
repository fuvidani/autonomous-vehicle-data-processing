package at.ac.tuwien.dse.ss18.group05.dto

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
data class Notification(
    val id: String,
    val concernedVehicles: List<String>,
    val message: String
)