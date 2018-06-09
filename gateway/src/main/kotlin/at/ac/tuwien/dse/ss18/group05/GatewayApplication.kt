package at.ac.tuwien.dse.ss18.group05

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.netflix.hystrix.EnableHystrix
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import reactor.core.publisher.Mono

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
@EnableHystrix
@RestController
class GatewayApplication : WebFluxConfigurer {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(GatewayApplication::class.java, *args)
        }
    }

    @RequestMapping("/vehicleFallback", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun vehicleFallback(): Mono<String> {
        return Mono.just("Fallback for vehicle service")
    }

    @RequestMapping("/trackingFallback", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun trackingFallback(): Mono<String> {
        return Mono.just("Fallback for tracking service")
    }

    @RequestMapping("/notificationFallback", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun notificationFallback(): Mono<String> {
        return Mono.just("Fallback for notification service")
    }

    @RequestMapping("/statisticsFallback", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun statisticsFallback(): Mono<String> {
        return Mono.just("Fallback for statistics service")
    }

    /**
     * Add resource handlers for serving static resources.
     * @see ResourceHandlerRegistry
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/documentation/**")
            .addResourceLocations("classpath:/static/docs/")
            .setCacheControl(CacheControl.noStore())
    }
}
