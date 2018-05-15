package at.ac.tuwien.dse.ss18.group05

/* ktlint-disable no-wildcard-imports */
import at.ac.tuwien.dse.ss18.group05.dto.*
import at.ac.tuwien.dse.ss18.group05.messaging.receiver.Receiver
import at.ac.tuwien.dse.ss18.group05.service.client.VehicleServiceClient
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeospatialIndex
import org.springframework.test.context.junit4.SpringRunner
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

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
class CompleteIntegrationTest {

    @Autowired
    @Qualifier("VehicleDataRecordReceiver")
    private lateinit var recordReceiver: Receiver
    @Autowired
    @Qualifier("EmergencyServiceMessageReceiver")
    private lateinit var emsReceiver: Receiver
    @MockBean
    private lateinit var vehicleServiceClient: VehicleServiceClient
    @MockBean
    private lateinit var rabbitTemplate: RabbitTemplate
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    @Suppress("unused")
    @MockBean
    private lateinit var rabbitAdmin: RabbitAdmin
    private val gson = Gson()

    @Before
    fun setUp() {
        Mockito.`when`(vehicleServiceClient.getAllVehicles()).thenReturn(getAllVehicles())
        mongoTemplate.dropCollection(VehicleLocation::class.java)
        mongoTemplate.dropCollection(LiveAccident::class.java)
        mongoTemplate.indexOps(VehicleLocation::class.java)
            .ensureIndex(GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE))
    }

    @Test
    fun testSimpleLocationUpdateShouldNotPublishAnythingToMessageQueue() {
        val dataRecordAudi = TestDataProvider.testVehicleDataRecordAudi()
        val dataRecordAcura = TestDataProvider.testVehicleDataRecordAcura()
        recordReceiver.receiveMessage(gson.toJson(dataRecordAudi))
        verifyNoMessagePublishedToMessageQueue()
        recordReceiver.receiveMessage(gson.toJson(dataRecordAcura))
        verifyNoMessagePublishedToMessageQueue()
    }

    @Test
    fun testLocationUpdateThenNearCrashEventShouldNotifyManufacturer() {
        val dataRecordNormal = recordVehicle3()
        recordReceiver.receiveMessage(gson.toJson(dataRecordNormal))
        verifyNoMessagePublishedToMessageQueue()

        val dataRecordNearCrash =
            TestDataProvider.testVehicleDataRecordNearCrashTesla(GpsLocation(16.3786159, 48.1739176))
        val expectedNotification = ManufacturerNotification(
            null,
            dataRecordNearCrash.timestamp,
            dataRecordNearCrash.metaData.identificationNumber,
            "Tesla",
            dataRecordNearCrash.metaData.model,
            dataRecordNearCrash.sensorInformation.location,
            EventInformation.NEAR_CRASH,
            null
        )
        recordReceiver.receiveMessage(gson.toJson(dataRecordNearCrash))
        Mockito.verify(rabbitTemplate, times(1))
            .convertAndSend("vehicle-data-exchange", "notifications.manufacturer", gson.toJson(expectedNotification))
        verifyNoMessagePublishedToMessageQueue()
    }

    @Test
    fun testCompleteScenario_Update_Crash_Arrival_SiteClearing() {
        Mockito.`when`(vehicleServiceClient.getAllVehicles()).thenReturn(getAllVehicles())
        recordReceiver.receiveMessage(gson.toJson(recordVehicle1()))
        recordReceiver.receiveMessage(gson.toJson(recordVehicle2()))
        recordReceiver.receiveMessage(gson.toJson(recordVehicle3()))
        recordReceiver.receiveMessage(gson.toJson(recordVehicle4()))
        recordReceiver.receiveMessage(gson.toJson(recordVehicle5()))
        recordReceiver.receiveMessage(gson.toJson(recordVehicle6()))
        recordReceiver.receiveMessage(gson.toJson(recordVehicle7()))
        recordReceiver.receiveMessage(gson.toJson(recordVehicle8()))
        recordReceiver.receiveMessage(gson.toJson(recordVehicle9()))
        verifyNoMessagePublishedToMessageQueue()
        Assert.assertEquals(9, mongoTemplate.findAll(VehicleLocation::class.java).size)

        // CRASH
        val now = Instant.now()
        val crashDataRecord = VehicleDataRecord(
            null, now.toEpochMilli(), MetaData("JH4DB8590SS001561", "Audi TT"),
            SensorInformation(
                GpsLocation(48.2081743, 16.3738189),
                ProximityInformation(0.0, 0.0), 4, 25.75
            ), EventInformation.CRASH
        )

        recordReceiver.receiveMessage(gson.toJson(crashDataRecord))

        val storedLiveAccident = mongoTemplate.findAll(LiveAccident::class.java)[0]
        val expectedManufacturerNotification = ManufacturerNotification(
            null,
            crashDataRecord.timestamp,
            crashDataRecord.metaData.identificationNumber,
            "Audi",
            crashDataRecord.metaData.model,
            crashDataRecord.sensorInformation.location,
            EventInformation.CRASH,
            storedLiveAccident.id
        )
        val expectedServiceNotification = EmergencyServiceNotification(
            null, storedLiveAccident.id!!, crashDataRecord.timestamp, crashDataRecord.sensorInformation.location,
            crashDataRecord.metaData.model, crashDataRecord.sensorInformation.passengers
        )
        val expectedVehicleNotification = VehicleNotification(
            arrayOf("1G1AS58H497251672", "1FTFW1EFXEFB07248", "4T1BG22K5XU921742"),
            arrayOf("JH4DB8590SS001561", "3GCPCSE03BG366866", "4T4BE46K19R123050"),
            storedLiveAccident.id!!,
            crashDataRecord.timestamp,
            crashDataRecord.sensorInformation.location,
            EmergencyServiceStatus.UNKNOWN,
            true,
            10.0
        )

        Mockito.verify(rabbitTemplate, times(1))
            .convertAndSend(
                "vehicle-data-exchange",
                "notifications.manufacturer",
                gson.toJson(expectedManufacturerNotification)
            )
        Mockito.verify(rabbitTemplate, times(1))
            .convertAndSend("vehicle-data-exchange", "notifications.ems", gson.toJson(expectedServiceNotification))
        Mockito.verify(rabbitTemplate, times(1))
            .convertAndSend("vehicle-data-exchange", "notifications.vehicle", gson.toJson(expectedVehicleNotification))

        // SERVICE ARRIVES
        val serviceArrival = now.plus(10, ChronoUnit.MINUTES).toEpochMilli()
        val emergencyServiceMessage =
            EmergencyServiceMessage(serviceArrival, storedLiveAccident.id!!, EmergencyServiceStatus.ARRIVED)

        emsReceiver.receiveMessage(gson.toJson(emergencyServiceMessage))
        val expectedVehicleNotificationOfArrival = VehicleNotification(
            arrayOf("1G1AS58H497251672", "1FTFW1EFXEFB07248", "4T1BG22K5XU921742"),
            arrayOf("JH4DB8590SS001561", "3GCPCSE03BG366866", "4T4BE46K19R123050"),
            storedLiveAccident.id!!,
            serviceArrival,
            crashDataRecord.sensorInformation.location,
            EmergencyServiceStatus.ARRIVED,
            true,
            10.0
        )
        Mockito.verify(rabbitTemplate, times(1))
            .convertAndSend(
                "vehicle-data-exchange",
                "notifications.vehicle",
                gson.toJson(expectedVehicleNotificationOfArrival)
            )

        // SITE CLEARED
        val timestampOfClearance = now.plus(30, ChronoUnit.MINUTES).toEpochMilli()
        val emergencyServiceMessageOfClearance =
            EmergencyServiceMessage(timestampOfClearance, storedLiveAccident.id!!, EmergencyServiceStatus.AREA_CLEARED)

        emsReceiver.receiveMessage(gson.toJson(emergencyServiceMessageOfClearance))
        val expectedVehicleNotificationOfClearance = VehicleNotification(
            arrayOf("1G1AS58H497251672", "1FTFW1EFXEFB07248", "4T1BG22K5XU921742"),
            arrayOf("JH4DB8590SS001561", "3GCPCSE03BG366866", "4T4BE46K19R123050"),
            storedLiveAccident.id!!,
            timestampOfClearance,
            crashDataRecord.sensorInformation.location,
            EmergencyServiceStatus.AREA_CLEARED,
            true,
            10.0
        )
        val expectedAccidentReport = AccidentReport(
            null, storedLiveAccident.id!!, crashDataRecord.metaData, crashDataRecord.sensorInformation.location,
            crashDataRecord.sensorInformation.passengers, TimeUnit.MINUTES.toMillis(10), TimeUnit.MINUTES.toMillis(20)
        )
        Mockito.verify(rabbitTemplate, times(1))
            .convertAndSend(
                "vehicle-data-exchange",
                "notifications.vehicle",
                gson.toJson(expectedVehicleNotificationOfClearance)
            )
        Mockito.verify(rabbitTemplate, times(1))
            .convertAndSend("vehicle-data-exchange", "statistics.report", gson.toJson(expectedAccidentReport))
        verifyNoMessagePublishedToMessageQueue()
    }

    private fun verifyNoMessagePublishedToMessageQueue() {
        Mockito.verify(rabbitTemplate, never())
            .convertAndSend(any(String::class.java), any(String::class.java), any(JvmType.Object::class.java))
    }

    // Kotlin<->Java Mockito type inference workaround
    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    private fun getAllVehicles(): List<Vehicle> {
        return listOf(
            getVehicle1(),
            getVehicle2(),
            getVehicle3(),
            getVehicle4(),
            getVehicle5(),
            getVehicle6(),
            getVehicle7(),
            getVehicle8(),
            getVehicle9()
        )
    }

    private fun getVehicle1(): Vehicle {
        return Vehicle("JH4DB8590SS001561", "Audi", "Audi TT")
    }

    private fun getVehicle2(): Vehicle {
        return Vehicle("3GCPCSE03BG366866", "Volkswagen", "Passat CC")
    }

    private fun getVehicle3(): Vehicle {
        return Vehicle("4T4BE46K19R123050", "Tesla", "Model X")
    }

    private fun getVehicle4(): Vehicle {
        return Vehicle("1G1AS58H497251672", "Fiat", "500")
    }

    private fun getVehicle5(): Vehicle {
        return Vehicle("1FTFW1EFXEFB07248", "BMW", "X6")
    }

    private fun getVehicle6(): Vehicle {
        return Vehicle("4T1BG22K5XU921742", "Skoda", "Superb")
    }

    private fun getVehicle7(): Vehicle {
        return Vehicle("1GNEC13T61R213970", "Skoda", "Fabia")
    }

    private fun getVehicle8(): Vehicle {
        return Vehicle("2HGFG3B5XDH517292", "Peugeot", "206")
    }

    private fun getVehicle9(): Vehicle {
        return Vehicle("JTMZD31V086081033", "Citroen", "Picasso")
    }

    private fun recordVehicle1(): VehicleDataRecord {
        return VehicleDataRecord(
            null, System.currentTimeMillis(), MetaData("JH4DB8590SS001561", "Audi TT"),
            SensorInformation(
                GpsLocation(48.20740925, 16.3735024),
                ProximityInformation(0.0, 0.0), 4, 25.75
            ), EventInformation.NONE
        )
    }

    private fun recordVehicle2(): VehicleDataRecord {
        return VehicleDataRecord(
            null, System.currentTimeMillis(), MetaData("3GCPCSE03BG366866", "Passat CC"),
            SensorInformation(
                GpsLocation(48.2080885, 16.37153902),
                ProximityInformation(0.0, 0.0), 4, 25.75
            ), EventInformation.NONE
        )
    }

    private fun recordVehicle3(): VehicleDataRecord {
        return VehicleDataRecord(
            null, System.currentTimeMillis(), MetaData("4T4BE46K19R123050", "Model X"),
            SensorInformation(
                GpsLocation(48.20891789, 16.37663522),
                ProximityInformation(0.0, 0.0), 4, 25.75
            ), EventInformation.NONE
        )
    }

    private fun recordVehicle4(): VehicleDataRecord {
        return VehicleDataRecord(
            null, System.currentTimeMillis(), MetaData("1G1AS58H497251672", "500"),
            SensorInformation(
                GpsLocation(48.18872267, 16.3811467),
                ProximityInformation(0.0, 0.0), 4, 25.75
            ), EventInformation.NONE
        )
    }

    private fun recordVehicle5(): VehicleDataRecord {
        return VehicleDataRecord(
            null, System.currentTimeMillis(), MetaData("1FTFW1EFXEFB07248", "X6"),
            SensorInformation(
                GpsLocation(48.18926627, 16.38449409),
                ProximityInformation(0.0, 0.0), 4, 25.75
            ), EventInformation.NONE
        )
    }

    private fun recordVehicle6(): VehicleDataRecord {
        return VehicleDataRecord(
            null, System.currentTimeMillis(), MetaData("4T1BG22K5XU921742", "Superb"),
            SensorInformation(
                GpsLocation(48.18840795, 16.38245561),
                ProximityInformation(0.0, 0.0), 4, 25.75
            ), EventInformation.NONE
        )
    }

    private fun recordVehicle7(): VehicleDataRecord {
        return VehicleDataRecord(
            null, System.currentTimeMillis(), MetaData("1GNEC13T61R213970", "Fabia"),
            SensorInformation(
                GpsLocation(47.96329872, 16.45693445),
                ProximityInformation(0.0, 0.0), 4, 25.75
            ), EventInformation.NONE
        )
    }

    private fun recordVehicle8(): VehicleDataRecord {
        return VehicleDataRecord(
            null, System.currentTimeMillis(), MetaData("2HGFG3B5XDH517292", "206"),
            SensorInformation(
                GpsLocation(47.95301753, 16.41949093),
                ProximityInformation(0.0, 0.0), 4, 25.75
            ), EventInformation.NONE
        )
    }

    private fun recordVehicle9(): VehicleDataRecord {
        return VehicleDataRecord(
            null, System.currentTimeMillis(), MetaData("JTMZD31V086081033", "Picasso"),
            SensorInformation(
                GpsLocation(47.87499551, 16.48427081),
                ProximityInformation(0.0, 0.0), 4, 25.75
            ), EventInformation.NONE
        )
    }
}