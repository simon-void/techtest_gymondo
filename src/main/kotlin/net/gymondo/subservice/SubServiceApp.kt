package net.gymondo.subservice

import net.gymondo.subservice.repository.*
import net.gymondo.subservice.service.Level
import net.gymondo.subservice.service.OfferDuration
import net.gymondo.subservice.service.OfferDurationUnit
import net.gymondo.subservice.service.SubscriptionService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.time.LocalDate

fun main(args: Array<String>) {
    runApplication<SubServiceApp>(*args)
}

@SpringBootApplication
@ConfigurationPropertiesScan("net.gymondo.subservice")
@EnableJpaRepositories("net.gymondo.subservice.repository")
class SubServiceApp(
    private val userRepo: UserRepository,
    private val courseRepo: CourseRepository,
    private val offerRepo: OfferRepository,
    private val subRepo: SubscriptionRepository,
    private val subService: SubscriptionService,
) : CommandLineRunner {

    companion object: Logging(SubServiceApp::class.java)

    override fun run(vararg args: String) {
        initDB()
    }

    private fun initDB() {
        val userId = userRepo.save(
            UserEntity(
                name = "user1",
                passwordHash = "some hash"
            )
        ).id!!

        val courseId = courseRepo.save(
            CourseEntity(
                name = "BestPilates",
                instructorName = "",
                level = Level.ADVANCED.toString(),
            )
        ).id!!

        val today = LocalDate.now()
        val twoWeekOfferId = offerRepo.save(
            OfferEntity(
                courseId,
                OfferDuration.with(OfferDurationUnit.WEEKS, 2).toString(),
                today,
                11999,
            )
        ).id!!
        val oneMonthOfferId = offerRepo.save(
            OfferEntity(
                courseId,
                OfferDuration.with(OfferDurationUnit.MONTHS, 1).toString(),
                today,
                19999,
            )
        ).id!!

        // add a subscription
        subService.subscribe(userId, oneMonthOfferId)

        // and print all found db data to the log
        fun Iterable<Any>.printContents(contentName: String) {
            val thisAsList = this.toList()
            logger.info("found ${thisAsList.size} $contentName:")
            for(item in thisAsList) {
                logger.info("$item")
            }
        }
        userRepo.findAll().printContents("users")
        courseRepo.findAll().printContents("courses")
        offerRepo.findAll().printContents("offers/products")
        subRepo.findAll().printContents("subscriptions")
    }
}
