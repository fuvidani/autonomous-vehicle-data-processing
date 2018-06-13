package at.ac.tuwien.dse.ss18.group05.messaging.sender

import at.ac.tuwien.dse.ss18.group05.TestDataProvider
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(SpringRunner::class)
class SenderTest {

    private lateinit var sender: Sender
    @MockBean
    private lateinit var rabbitTemplate: RabbitTemplate
    private val gson = Gson()

    @Before
    fun setUp() {
        sender = Sender(rabbitTemplate, gson)
    }

    @Test
    fun testSendVehicleNotificationShouldPlaceItOnBus() {
        val notification = TestDataProvider.testVehicleNotificationCrash()
        sender.sendMessage(notification, "notifications.vehicle")
        Mockito.verify(rabbitTemplate)
            .convertAndSend("vehicle-data-exchange", "notifications.vehicle", gson.toJson(notification))
    }

    @Test
    fun testSendEmergencyServiceNotificationShouldPlaceItOnBus() {
        val notification = TestDataProvider.testEmergencyServiceNotification()
        sender.sendMessage(notification, "notifications.ems")
        Mockito.verify(rabbitTemplate)
            .convertAndSend("vehicle-data-exchange", "notifications.ems", gson.toJson(notification))
    }

    @Test
    fun testSendManufacturerNotificationShouldPlaceItOnBus() {
        val notification = TestDataProvider.testManufacturerNotification()
        sender.sendMessage(notification, "notifications.manufacturer")
        Mockito.verify(rabbitTemplate)
            .convertAndSend("vehicle-data-exchange", "notifications.manufacturer", gson.toJson(notification))
    }

    @Test
    fun testSendAccidentReportShouldPlaceItOnBus() {
        val report = TestDataProvider.testAccidentReport()
        sender.sendMessage(report, "statistics.report")
        Mockito.verify(rabbitTemplate).convertAndSend("vehicle-data-exchange", "statistics.report", gson.toJson(report))
    }
}