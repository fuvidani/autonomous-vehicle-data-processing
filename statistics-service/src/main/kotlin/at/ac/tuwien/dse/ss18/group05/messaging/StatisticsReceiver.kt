package at.ac.tuwien.dse.ss18.group05.messaging

import at.ac.tuwien.dse.ss18.group05.dto.AccidentReport
import at.ac.tuwien.dse.ss18.group05.service.StatisticsService
import com.google.gson.Gson
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
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
@Component
class StatisticsReceiver(
    private val gson: Gson,
    private val statisticsService: StatisticsService
) : Receiver {

    private val log = Logger.getLogger(this.javaClass.name)

    @RabbitListener(queues = ["#{statisticsQueue.name}"])
    override fun receiveMessage(message: String) {
        log.info("New statistics arrived")
        val report = gson.fromJson<AccidentReport>(message, AccidentReport::class.java)
        log.info(report.toString())
        statisticsService.create(report).subscribe()
    }
}