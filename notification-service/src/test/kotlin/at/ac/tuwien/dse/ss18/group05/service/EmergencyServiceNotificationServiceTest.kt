package at.ac.tuwien.dse.ss18.group05.service

import at.ac.tuwien.dse.ss18.group05.TestDataGenerator
import at.ac.tuwien.dse.ss18.group05.dto.EmergencyServiceNotification
import at.ac.tuwien.dse.ss18.group05.messaging.Receiver
import at.ac.tuwien.dse.ss18.group05.repository.EmergencyServiceNotificationRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
class EmergencyServiceNotificationServiceTest {

    @MockBean
    private lateinit var repository: EmergencyServiceNotificationRepository

    @MockBean
    private lateinit var receiver: Receiver<EmergencyServiceNotification>

    private lateinit var service: IEmergencyServiceNotificationService

    private val generator = TestDataGenerator()

    @Before
    fun setUp() {

        Mockito.`when`(repository.findAll()).thenReturn(Flux.fromArray(generator.getAllEMSNotifications()))
        Mockito.`when`(receiver.notificationStream()).thenReturn(Flux.fromArray(generator.getAllEMSNotifications()))

        service = EmergencyServiceNotificationService(receiver, repository)
    }

    @Test
    fun findAll_findingAllStoredNotifications_shouldReturnInInsertionOrder() {
        StepVerifier
                .create(service.findAllHistoryNotifications())
                .expectNext(generator.getFirstEMSNotification())
                .expectNext(generator.getSecondEMSNotification())
                .verifyComplete()
    }

    @Test
    fun streamNotifications_streamingWithEmptyNotifications_shouldReturnEmpty() {
        Mockito.`when`(receiver.notificationStream()).thenReturn(Flux.empty())
        StepVerifier
                .create(service.streamEmsNotifications())
                .expectSubscription()
                .verifyComplete()
    }

    @Test
    fun handleNotification_publishingNotification_shouldGetStoredAndStreamed() {
        StepVerifier
                .create(service.streamEmsNotifications())
                .expectSubscription()
                .expectNext(generator.getFirstEMSNotification())
                .expectNext(generator.getSecondEMSNotification())
                .verifyComplete()

        StepVerifier
                .create(service.findAllHistoryNotifications())
                .expectNext(generator.getFirstEMSNotification())
                .expectNext(generator.getSecondEMSNotification())
                .verifyComplete()
    }
}