package at.ac.tuwien.dse.ss18.group05.messaging

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

interface Receiver<T> {

    fun receiveMessage(message: String)

    fun notificationStream(): Flux<T>
}

@Component
class VehicleNotificationReceiver(
    private val repository: VehicleNotificationRepository,
    private val processor: TopicProcessor<VehicleNotification>,
    private val gson: Gson
) : Receiver<VehicleNotification> {

    private val log = Logger.getLogger(this.javaClass.name)

    @RabbitListener(queues = ["#{vehicleQueue.name}"])
    override fun receiveMessage(message: String) {
        val incomingVehicleNotification = gson.fromJson(message, IncomingVehicleNotification::class.java)
        log.info("received notification for vehicles \nnear by: ${incomingVehicleNotification.concernedNearByVehicles} \nfar away: ${incomingVehicleNotification.concernedFarAwayVehicles}")
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

@Component
class EmsNotificationReceiver(
    private val repository: EmergencyServiceNotificationRepository,
    private val processor: TopicProcessor<EmergencyServiceNotification>,
    private val gson: Gson
) : Receiver<EmergencyServiceNotification> {

    private val log = Logger.getLogger(this.javaClass.name)

    @RabbitListener(queues = ["#{emsQueue.name}"])
    override fun receiveMessage(message: String) {
        val emergencyServiceNotification = gson.fromJson(message, EmergencyServiceNotification::class.java)
        log.info("received ems notification $emergencyServiceNotification")
        repository.save(emergencyServiceNotification).subscribe { processor.onNext(it) }
    }

    override fun notificationStream(): Flux<EmergencyServiceNotification> {
        return processor
    }
}

@Component
class ManufacturerNotifictionReceiver(
    private val repository: ManufacturerNotificationRepository,
    private val processor: TopicProcessor<ManufacturerNotification>,
    private val gson: Gson
) : Receiver<ManufacturerNotification> {

    private val log = Logger.getLogger(this.javaClass.name)

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
