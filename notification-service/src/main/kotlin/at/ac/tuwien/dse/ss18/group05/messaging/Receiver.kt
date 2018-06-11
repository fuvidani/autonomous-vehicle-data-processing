package at.ac.tuwien.dse.ss18.group05.messaging

/* ktlint-disable no-wildcard-imports */

import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.IncomingVehicleNotification
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.dto.VehicleNotification
import at.ac.tuwien.dse.ss18.group05.repository.EmergencyServiceNotificationRepository
import at.ac.tuwien.dse.ss18.group05.repository.ManufacturerNotificationRepository
import at.ac.tuwien.dse.ss18.group05.repository.VehicleNotificationRepository
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
import java.util.*
import java.util.logging.Logger

/**
 * receiver interface for all possible concrete receivers
 * need to be able to actualy receive messages and also
 * expose the received messages as a flux
 */
interface Receiver<T> {

    /**
     * receive the message in string format and then further process it
     */
    fun receiveMessage(message: String)

    /**
     * stream all the notifications of the class as a flux object
     */
    fun notificationStream(): Flux<T>
}

/**
 * handling all vehicle specific notifications here
 */
@Component
class VehicleNotificationReceiver(
    private val repository: VehicleNotificationRepository,
    private val processor: TopicProcessor<VehicleNotification>,
    private val gson: Gson
) : Receiver<VehicleNotification> {

    private val log = Logger.getLogger(this.javaClass.name)

    /**
     * Receives the provided message.
     *
     * @param message an arbitrary message in String format
     */
    @RabbitListener(queues = ["#{vehicleQueue.name}"])
    override fun receiveMessage(message: String) {
        val incomingVehicleNotification = gson.fromJson(message, IncomingVehicleNotification::class.java)
        log.info("received notification for vehicles near by: ${Arrays.toString(incomingVehicleNotification.concernedNearByVehicles)} far away: ${Arrays.toString(incomingVehicleNotification.concernedFarAwayVehicles)}")
        handleNotifications(incomingVehicleNotification, incomingVehicleNotification.concernedNearByVehicles)
        handleFieldsForFarAwayVehicles(incomingVehicleNotification)
        handleNotifications(incomingVehicleNotification, incomingVehicleNotification.concernedFarAwayVehicles)
    }

    private fun handleFieldsForFarAwayVehicles(incomingVehicleNotification: IncomingVehicleNotification) {
        incomingVehicleNotification.targetSpeed = null
        incomingVehicleNotification.specialWarning = null
    }

    private fun handleNotifications(incomingVehicleNotification: IncomingVehicleNotification, ids: Array<String>) {
        ids.forEach {
            val vehicleNotification = VehicleNotification(vehicleId = it, incomingVehicleNotification = incomingVehicleNotification)
            repository.save(vehicleNotification).subscribe { processor.onNext(it) }
        }
    }

    override fun notificationStream(): Flux<VehicleNotification> {
        return processor
    }
}

/**
 * handling all emergency service specific notifications here
 */
@Component
class EmsNotificationReceiver(
    private val repository: EmergencyServiceNotificationRepository,
    private val processor: TopicProcessor<EmergencyServiceNotification>,
    private val gson: Gson
) : Receiver<EmergencyServiceNotification> {

    private val log = Logger.getLogger(this.javaClass.name)

    /**
     * Receives the provided message.
     *
     * @param message an arbitrary message in String format
     */
    @RabbitListener(queues = ["#{emsQueue.name}"])
    override fun receiveMessage(message: String) {
        val emergencyServiceNotification = gson.fromJson(message, EmergencyServiceNotification::class.java)
        log.info("EMS - received notification $emergencyServiceNotification")

        val existingNotification = repository.findByAccidentId(emergencyServiceNotification.accidentId).block()
        if (existingNotification != null) {
            existingNotification.status = emergencyServiceNotification.status
            repository.save(existingNotification).subscribe { processor.onNext(it) }
        } else {
            repository.save(emergencyServiceNotification).subscribe { processor.onNext(it) }
        }
    }

    override fun notificationStream(): Flux<EmergencyServiceNotification> {
        return processor
    }
}

/**
 * handling all manufacturer specific notifications here
 */
@Component
class ManufacturerNotifictionReceiver(
    private val repository: ManufacturerNotificationRepository,
    private val processor: TopicProcessor<ManufacturerNotification>,
    private val gson: Gson
) : Receiver<ManufacturerNotification> {

    private val log = Logger.getLogger(this.javaClass.name)

    /**
     * Receives the provided message.
     *
     * @param message an arbitrary message in String format
     */
    @RabbitListener(queues = ["#{manufacturerQueue.name}"])
    override fun receiveMessage(message: String) {
        val notification = gson.fromJson(message, ManufacturerNotification::class.java)
        log.info("received manufacturer notification")
        repository.save(notification).subscribe { processor.onNext(it) }
    }

    override fun notificationStream(): Flux<ManufacturerNotification> {
        return processor
    }
}
