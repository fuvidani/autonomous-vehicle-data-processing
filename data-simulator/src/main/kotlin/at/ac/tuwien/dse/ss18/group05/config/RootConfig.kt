package at.ac.tuwien.dse.ss18.group05.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class RootConfig {

    @Bean
    fun client(): WebClient {
        return WebClient.create("http://localhost:4000/")
    }
}