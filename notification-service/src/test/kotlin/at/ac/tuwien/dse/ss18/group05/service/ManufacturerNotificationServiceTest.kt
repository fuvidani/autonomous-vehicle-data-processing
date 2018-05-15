package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.ManufacturerNotification
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.ManufacturerNotificationRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
class ManufacturerNotificationServiceTest {

    private val manufacturerAId = "man_a_id"
    private val manufacturerBId = "man_b_id"

    @MockBean
    private lateinit var repository: ManufacturerNotificationRepository

    @MockBean
    private lateinit var receiver: Receiver<ManufacturerNotification>

    private lateinit var service: IManufacturerNotificationService

    private val generator = TestDataGenerator()

    @Before
    fun setUp() {

        val manufacturerANotifications = generator.getAllManufacturerNotifications().filter { n -> n.manufacturerId == manufacturerAId }
        val manufacturerBNotifications = generator.getAllManufacturerNotifications().filter { n -> n.manufacturerId == manufacturerBId }
        Mockito.`when`(repository.findByManufacturerId(manufacturerAId)).thenReturn(Flux.fromIterable(manufacturerANotifications))
        Mockito.`when`(repository.findByManufacturerId(manufacturerBId)).thenReturn(Flux.fromIterable(manufacturerBNotifications))
        Mockito.`when`(receiver.notificationStream()).thenReturn(Flux.fromArray(generator.getAllManufacturerNotifications()))

        service = ManufacturerNotificationService(receiver, repository)
    }

    @Test
    fun findAll_findingAllStoredNotifications_shouldReturnInInsertionOrder() {
        StepVerifier
                .create(service.findAllHistoryNotifications(manufacturerAId))
                .expectNext(generator.getManufacturerAFirstNotification())
                .expectNext(generator.getManufacturerASecondNotification())
                .verifyComplete()

        StepVerifier
                .create(service.findAllHistoryNotifications(manufacturerBId))
                .expectNext(generator.getManufacturerBFirstNotification())
                .expectNext(generator.getManufacturerBSecondNotification())
                .verifyComplete()
    }

    @Test
    fun streamNotifications_streamingWithEmptyNotifications_shouldReturnEmpty() {
        Mockito.`when`(receiver.notificationStream()).thenReturn(Flux.empty())
        StepVerifier
                .create(service.streamManufacturerNotifications(manufacturerAId))
                .expectSubscription()
                .verifyComplete()
    }

    @Test
    fun streamNotifications_streamingNotificationsForManufacturers_shouldReturnExpectedNotifications() {
        val manufacturerAFlux = service.streamManufacturerNotifications(manufacturerAId)
        val manufacturerBFlux = service.streamManufacturerNotifications(manufacturerBId)
        val combined = manufacturerAFlux.mergeWith(manufacturerBFlux)
        StepVerifier
                .create(combined)
                .expectSubscription()
                .expectNext(generator.getManufacturerAFirstNotification())
                .expectNext(generator.getManufacturerASecondNotification())
                .expectNext(generator.getManufacturerBFirstNotification())
                .expectNext(generator.getManufacturerBSecondNotification())
                .expectComplete()
                .verify()
    }

    @Test
    fun streamNotifications_streamingNotificationsForManufacturerA_shouldReturnExpectedNotifications() {
        val manufacturerAFlux = service.streamManufacturerNotifications(manufacturerAId)
        StepVerifier
                .create(manufacturerAFlux)
                .expectSubscription()
                .expectNext(generator.getManufacturerAFirstNotification())
                .expectNext(generator.getManufacturerASecondNotification())
                .expectComplete()
                .verify()
    }

    @Test
    fun streamNotifications_streamingNotificationsForManufacturerB_shouldReturnExpectedNotifications() {
        val manufacturerBFlux = service.streamManufacturerNotifications(manufacturerBId)
        StepVerifier
                .create(manufacturerBFlux)
                .expectSubscription()
                .expectNext(generator.getManufacturerBFirstNotification())
                .expectNext(generator.getManufacturerBSecondNotification())
                .expectComplete()
                .verify()
    }
}