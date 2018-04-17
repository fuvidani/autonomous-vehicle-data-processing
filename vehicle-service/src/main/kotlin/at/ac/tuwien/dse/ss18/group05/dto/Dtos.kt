package at.ac.tuwien.dse.ss18.group05.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@Document(collection = "vehicles")
data class Vehicle(
    @Id
    val identificationNumber: String,
    val manufacturerId: String,
    val model: String
)

@Document(collection = "manufacturers")
data class Manufacturer(
    @Id
    val id: String?,
    val label: String
)