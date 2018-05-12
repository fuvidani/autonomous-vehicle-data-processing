package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.dto.VehicleDataRecord
import at.ac.tuwien.dse.ss18.group05.service.client.VehicleService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.context.annotation.Bean
import org.springframework.http.CacheControl
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import reactor.core.publisher.TopicProcessor
import reactor.util.concurrent.Queues
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableCircuitBreaker
class TrackerServiceApplication : WebFluxConfigurer {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(TrackerServiceApplication::class.java, *args)
        }
    }

    @Bean
    fun vehicleService(
        @Value("\${vehicle.service.host}") host: String,
        @Value("\${vehicle.service.port}") port: String
    ): VehicleService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$host:$port/vehicle/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(VehicleService::class.java)
    }

    @Bean
    fun vehicleDataRecordStreamProcessor(): TopicProcessor<VehicleDataRecord> {
        return TopicProcessor.builder<VehicleDataRecord>()
            .autoCancel(false)
            .share(true)
            .name("something")
            .bufferSize(Queues.SMALL_BUFFER_SIZE)
            .build()
    }

    /**
     * Add resource handlers for serving static resources.
     * @see ResourceHandlerRegistry
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/tracking/resources/**")
            .addResourceLocations("classpath:/static/docs/")
            .setCacheControl(CacheControl.noStore())
    }
}