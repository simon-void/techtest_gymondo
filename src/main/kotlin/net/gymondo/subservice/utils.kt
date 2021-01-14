package net.gymondo.subservice

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

fun interface ResourceLoader {
    fun load(path: String): String
}

@Configuration
class UtilsConfig {

    @Bean("utf8ResourceLoader")
    fun utf8ResourceLoader() = ResourceLoader { fileName ->
        val resource = ClassPathResource(fileName)
        resource.inputStream.reader().readText()
    }
}