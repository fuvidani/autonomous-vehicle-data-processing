package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.NotificationServiceTestApplication
import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.repository.EmergencyServiceNotificationRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
import reactor.test.StepVerifier

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
@SpringBootTest(value = ["application.yml"], classes = [NotificationServiceTestApplication::class])
class EmergencyServiceNotificationServiceTest {

    @MockBean
    private lateinit var admin: RabbitAdmin

    @Autowired
    private lateinit var repository: EmergencyServiceNotificationRepository

    private lateinit var service: IEmergencyServiceNotificationService

    @Autowired
    private lateinit var template: MongoTemplate

    private val generator = TestDataGenerator()

    @Before
    fun setUp() {
        service = EmergencyServiceNotificationService(repository)
        template.dropCollection(EmergencyServiceNotification::class.java)
        val emsNotifications = generator.getAllEMSNotifications()
        emsNotifications.forEach {
            template.insert(it)
        }
    }

    @Test
    fun findAll_findingAllStoredNotifications_shouldReturnInInsertionOrder() {
        StepVerifier
                .create(service.findAll())
                .expectNext(generator.getFirstEMSNotification())
                .expectNext(generator.getSecondEMSNotification())
                .verifyComplete()
    }

    @Test
    fun streamNotifications_streamingWithEmptyNotifications_shouldReturnEmpty() {
        val processor = service.streamEmsNotifications()
        StepVerifier
                .create(processor)
                .expectSubscription()
                .then {
                    completeProcessor(processor)
                }
                .verifyComplete()
    }

    @Test(timeout = 2500)
    fun handleNotification_publishingNotification_shouldGetStoredAndStreamed() {

        val notification = EmergencyServiceNotification(id = "random_id", accidentId = "a_ic", timeStamp = 1L, location = GpsLocation(0.0, 0.0), model = "new_model", passengers = 2)

        val processor = service.streamEmsNotifications()

        StepVerifier
                .create(processor)
                .expectSubscription()
                .then {
                    service.handleEmsNotification(notification)
                    completeProcessor(processor)
                }
                .expectNext(notification)
                .verifyComplete()

        StepVerifier
                .create(service.findAll())
                .expectNext(generator.getFirstEMSNotification())
                .expectNext(generator.getSecondEMSNotification())
                .expectNext(notification)
                .verifyComplete()
    }

    private fun completeProcessor(processor: Flux<EmergencyServiceNotification>) {
        if (processor is TopicProcessor<EmergencyServiceNotification>) {
            processor.onComplete()
        }
    }
}