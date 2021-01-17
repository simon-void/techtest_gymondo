package net.gymondo.subservice

import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

open class Logging(forClass: Class<*>) {
    protected val logger: Logger = LoggerFactory.getLogger(forClass)
}