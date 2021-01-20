package net.gymondo.subservice

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "localization")
data class LocalizationProperties(val taxPercentage: Double)