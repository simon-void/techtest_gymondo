package net.gymondo.subservice

import net.gymondo.subservice.repository.SubscriptionEntity
import net.gymondo.subservice.repository.SubscriptionRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.time.LocalDate

fun main(args: Array<String>) {
    runApplication<SubServiceApp>(*args)
}

@SpringBootApplication
@EnableJpaRepositories("net.gymondo.subservice.repository")
class SubServiceApp(
    val subsRepo: SubscriptionRepository
) : CommandLineRunner {

    companion object: Logging(SubServiceApp::class.java)

    override fun run(vararg args: String) {
        // add a subscription
        subsRepo.save(
            SubscriptionEntity(
                3L, 5L, "a while", 234, LocalDate.now()
            )
        )

        // and print all found subscriptions to the log
        val subscriptions: List<SubscriptionEntity> = subsRepo.findAll().toList()
        logger.info("found ${subscriptions.size} subscriptions:")
        for(sub in subscriptions) {
            logger.info("$sub")
        }
    }
}
