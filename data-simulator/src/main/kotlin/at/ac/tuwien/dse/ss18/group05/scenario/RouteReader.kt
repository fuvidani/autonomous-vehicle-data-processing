package at.ac.tuwien.dse.ss18.group05.scenario

import at.ac.tuwien.dse.ss18.group05.dto.RouteRecord
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.InputStreamReader

class RouteReader(private val filePath: String) {

    fun readRecords(): List<RouteRecord> {
        val inputStream = RouteReader::class.java.classLoader.getResourceAsStream(filePath)
        val reader = InputStreamReader(inputStream)
        val records = CSVFormat.EXCEL.withHeader("lat", "lon", "distance_to_start").parse(reader)
        return extractRecords(records)
    }

    private fun extractRecords(records: CSVParser): List<RouteRecord> {
        val routeRecords = mutableListOf<RouteRecord>()
        records.forEach { record -> routeRecords.add(extractRecord(record)) }
        return routeRecords
    }

    private fun extractRecord(record: CSVRecord): RouteRecord {
        val lat = record.get("lat").toDouble()
        val lon = record.get("lon").toDouble()
        val distance = record.get("distance_to_start").toDouble()
        return RouteRecord(lat, lon, distance)
    }
}