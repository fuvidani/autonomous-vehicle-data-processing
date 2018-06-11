package at.ac.tuwien.dse.ss18.group05.scenario
/* ktlint-disable no-wildcard-imports */

import at.ac.tuwien.dse.ss18.group05.dto.Vehicle
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.ClassPathResource
import java.util.*

/**
 * simple reader class which uses the newly introduced property source from spring boot 2.0
 */
class VehiclesReader(private val filePath: String) {

    fun getVehicles(): List<Vehicle> {
        val yml = loadYml()
        val propertySources = MutablePropertySources()
        propertySources.addFirst(PropertiesPropertySource("vehicles", yml))
        val sources = MapConfigurationPropertySource(yml)
        return Binder(sources).bind("vehicles", Bindable.listOf(Vehicle::class.java)).get()
    }

    private fun loadYml(): Properties {
        val properties = YamlPropertiesFactoryBean()
        properties.setResources(ClassPathResource(filePath))
        return properties.`object`!!
    }
}