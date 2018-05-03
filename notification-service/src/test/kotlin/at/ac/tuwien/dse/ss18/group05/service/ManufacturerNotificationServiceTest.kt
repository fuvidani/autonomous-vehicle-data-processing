package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.NotificationServiceTestApplication
import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EventInformation
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.repository.ManufacturerNotificationRepository
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

@RunWith(SpringRunner::class)
@SpringBootTest(value = ["application.yml"], classes = [NotificationServiceTestApplication::class])
class ManufacturerNotificationServiceTest {

    @MockBean
    private lateinit var admin: RabbitAdmin

    @Autowired
    private lateinit var repository: ManufacturerNotificationRepository

    private lateinit var service: IManufacturerNotificationService

    @Autowired
    private lateinit var template: MongoTemplate

    private val generator = TestDataGenerator()

    @Before
    fun setUp() {
        service = ManufacturerNotificationService(repository)
        template.dropCollection(ManufacturerNotification::class.java)
        val manufacturerNotifications = generator.getAllManufacturerNotifications()
        manufacturerNotifications.forEach {
            template.insert(it)
        }
    }

    @Test
    fun findAll_findingAllStoredNotifications_shouldReturnInInsertionOrder() {
        val manufacturerAId = "man_a_id"
        val manufacturerBId = "man_b_id"

        StepVerifier
                .create(service.findAllForManufacturer(manufacturerAId))
                .expectNext(generator.getManufacturerAFirstNotification())
                .expectNext(generator.getManufacturerASecondNotification())
                .verifyComplete()

        StepVerifier
                .create(service.findAllForManufacturer(manufacturerBId))
                .expectNext(generator.getManufacturerBFirstNotification())
                .expectNext(generator.getManufacturerBSecondNotification())
                .verifyComplete()
    }

    @Test
    fun streamNotifications_streamingWithEmptyNotifications_shouldReturnEmpty() {
        val processor = service.streamManufacturerNotifications()
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
        val manufacturerId = "man_c_id"
        val notification = ManufacturerNotification(id = "unique_id", timeStamp = 0L, vehicleIdentificationNumber = "new_id", manufacturerId = manufacturerId, model = "new_model", location = GpsLocation(0.0, 0.0), eventInfo = EventInformation.NONE, accidentId = "")

        val processor = service.streamManufacturerNotifications()

        StepVerifier
                .create(processor)
                .expectSubscription()
                .then {
                    service.handleManufacturerNotification(notification)
                    completeProcessor(processor)
                }
                .expectNext(notification)
                .verifyComplete()

        StepVerifier
                .create(service.findAllForManufacturer(manufacturerId))
                .expectNext(notification)
                .verifyComplete()
    }

    private fun completeProcessor(processor: Flux<ManufacturerNotification>) {
        if (processor is TopicProcessor<ManufacturerNotification>) {
            processor.onComplete()
        }
    }
}