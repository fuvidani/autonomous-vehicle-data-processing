package at.ac.tuwien.dse.ss18.group05

/* ktlint-disable no-wildcard-imports */

import at.ac.tuwien.dse.ss18.group05.dto.*

class TestDataGenerator {

    private val accidentLocation = GpsLocation(43.12, 12.00)
    private val firstVehicleId = "first_vehicle_id"
    private val secondVehicleId = "second_vehicle_id"
    private val firstManufacturer = "man_a_id"
    private val secondManufacturer = "man_b_id"

    fun getFirstEMSNotification(): EmergencyServiceNotification {
        return EmergencyServiceNotification(id = "first_id", accidentId = "first_accident", timeStamp = getTimeStamp(), location = accidentLocation, model = "some_model", passengers = 5, status = EmergencyServiceStatus.UNKNOWN)
    }

    fun getSecondEMSNotification(): EmergencyServiceNotification {
        return EmergencyServiceNotification(id = "second_id", accidentId = "second_accident", timeStamp = getTimeStamp(), location = accidentLocation, model = "some_model", passengers = 1,status = EmergencyServiceStatus.UNKNOWN)
    }

    fun getAllEMSNotifications(): Array<EmergencyServiceNotification> {
        return arrayOf(getFirstEMSNotification(), getSecondEMSNotification())
    }

    fun getManufacturerAFirstNotification(): ManufacturerNotification {
        return ManufacturerNotification(id = "first_id", timeStamp = getTimeStamp(), vehicleIdentificationNumber = firstVehicleId, manufacturerId = firstManufacturer, model = "some_model", location = accidentLocation, eventInfo = EventInformation.NONE, accidentId = "")
    }

    fun getManufacturerASecondNotification(): ManufacturerNotification {
        return ManufacturerNotification(id = "second_id", timeStamp = getTimeStamp(), vehicleIdentificationNumber = firstVehicleId, manufacturerId = firstManufacturer, model = "some_model", location = accidentLocation, eventInfo = EventInformation.CRASH, accidentId = "first_accident")
    }

    fun getManufacturerBFirstNotification(): ManufacturerNotification {
        return ManufacturerNotification(id = "third_id", timeStamp = getTimeStamp(), vehicleIdentificationNumber = secondVehicleId, manufacturerId = secondManufacturer, model = "some_model", location = accidentLocation, eventInfo = EventInformation.NONE, accidentId = "")
    }

    fun getManufacturerBSecondNotification(): ManufacturerNotification {
        return ManufacturerNotification(id = "fourth_id", timeStamp = getTimeStamp(), vehicleIdentificationNumber = secondVehicleId, manufacturerId = secondManufacturer, model = "some_model", location = accidentLocation, eventInfo = EventInformation.CRASH, accidentId = "second_accident")
    }

    fun getAllManufacturerNotifications(): Array<ManufacturerNotification> {
        return arrayOf(
                getManufacturerAFirstNotification(),
                getManufacturerASecondNotification(),
                getManufacturerBFirstNotification(),
                getManufacturerBSecondNotification()
        )
    }

    fun getVehicleAFirstNotification(): VehicleNotification {
        return VehicleNotification(id = null, vehicleIdentificationNumber = firstVehicleId, accidentId = "acc_id", timestamp = getTimeStamp(), location = accidentLocation, emergencyServiceStatus = EmergencyServiceStatus.UNKNOWN, specialWarning = null, targetSpeed = null)
    }

    fun getVehicleASecondNotification(): VehicleNotification {
        return VehicleNotification(id = null, vehicleIdentificationNumber = firstVehicleId, accidentId = "first_accident", timestamp = getTimeStamp(), location = accidentLocation, emergencyServiceStatus = EmergencyServiceStatus.ARRIVED, specialWarning = true, targetSpeed = 30.0)
    }

    fun getVehicleBFirstNotification(): VehicleNotification {
        return VehicleNotification(id = null, vehicleIdentificationNumber = secondVehicleId, accidentId = "acc_id", timestamp = getTimeStamp(), location = accidentLocation, emergencyServiceStatus = EmergencyServiceStatus.UNKNOWN, specialWarning = null, targetSpeed = null)
    }

    fun getVehicleBSecondNotification(): VehicleNotification {
        return VehicleNotification(id = null, vehicleIdentificationNumber = secondVehicleId, accidentId = "second_accident", timestamp = getTimeStamp(), location = accidentLocation, emergencyServiceStatus = EmergencyServiceStatus.ARRIVED, specialWarning = true, targetSpeed = 30.0)
    }

    fun getAllVehicleNotifications(): Array<VehicleNotification> {
        return arrayOf(
                getVehicleAFirstNotification(),
                getVehicleASecondNotification(),
                getVehicleBFirstNotification(),
                getVehicleBSecondNotification()
        )
    }

    private fun getTimeStamp(): Long {
        return 1L
    }
}