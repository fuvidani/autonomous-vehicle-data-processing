package at.ac.tuwien.dse.ss18.group05.repository

import at.ac.tuwien.dse.ss18.group05.DataProcessorApplication
import at.ac.tuwien.dse.ss18.group05.TestDataProvider
/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
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
@SpringBootTest(value = ["application.yml"], classes = [DataProcessorApplication::class])
class AccidentRepositoryTest {

    @Autowired
    private lateinit var repository: LiveAccidentRepository
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin

    @Before
    fun setUp() {
        mongoTemplate.dropCollection(LiveAccident::class.java)
    }

    @Test
    fun testSaveShouldAddIdAutomatically() {
        val newOngoingAccident = TestDataProvider.testLiveAccident()
        val savedAccident = repository.save(newOngoingAccident).block()
        Assert.assertNotNull(savedAccident)
        Assert.assertNotNull(savedAccident?.id)
        Assert.assertEquals(newOngoingAccident.vehicleMetaData, savedAccident?.vehicleMetaData)
        Assert.assertEquals(newOngoingAccident.location, savedAccident?.location)
        Assert.assertEquals(newOngoingAccident.timestampOfAccident, savedAccident?.timestampOfAccident)
    }

    @Test
    fun testSaveThroughVehicleDataRecord() {
        val crashDataRecord = TestDataProvider.testVehicleDataRecordCrashTesla()
        val savedAccident = repository.save(crashDataRecord.toDefaultLiveAccident()).block()
        Assert.assertNotNull(savedAccident)
        Assert.assertNotNull(savedAccident?.id)
        Assert.assertEquals(crashDataRecord.metaData, savedAccident?.vehicleMetaData)
        Assert.assertEquals(crashDataRecord.sensorInformation.location.lon, savedAccident?.location?.x)
        Assert.assertEquals(crashDataRecord.sensorInformation.location.lat, savedAccident?.location?.y)
        Assert.assertEquals(crashDataRecord.timestamp, savedAccident?.timestampOfAccident)
    }

    @Test
    fun testLiveAccidentLifecycle() {
        val crashDataRecord = TestDataProvider.testVehicleDataRecordCrashTesla()
        val savedNewAccident = repository.save(crashDataRecord.toDefaultLiveAccident()).block()
        Assert.assertNotNull(savedNewAccident)
        Assert.assertNotNull(savedNewAccident?.id)

        val accidentWithArrival = repository.save(savedNewAccident!!.withServiceArrival(15)).block()
        Assert.assertNotNull(accidentWithArrival)
        Assert.assertNotNull(accidentWithArrival?.id)
        Assert.assertEquals(savedNewAccident.id, accidentWithArrival!!.id)
        Assert.assertEquals(savedNewAccident.timestampOfAccident, accidentWithArrival.timestampOfAccident)
        Assert.assertEquals(15L, accidentWithArrival.timestampOfServiceArrival)

        val accidentWithSiteClearing = repository.save(accidentWithArrival.withSiteClearing(99)).block()
        Assert.assertNotNull(accidentWithSiteClearing)
        Assert.assertNotNull(accidentWithSiteClearing?.id)
        Assert.assertEquals(accidentWithArrival.id, accidentWithSiteClearing!!.id)
        Assert.assertEquals(accidentWithArrival.timestampOfAccident, accidentWithSiteClearing.timestampOfAccident)
        Assert.assertEquals(
            accidentWithArrival.timestampOfServiceArrival,
            accidentWithSiteClearing.timestampOfServiceArrival
        )
        Assert.assertEquals(99L, accidentWithSiteClearing.timestampOfSiteClearing)
    }
}