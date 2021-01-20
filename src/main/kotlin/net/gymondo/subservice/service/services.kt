package net.gymondo.subservice.service

import net.gymondo.subservice.Logging
import net.gymondo.subservice.repository.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class OfferService(
    private val offerRepo: OfferRepository,
) {
    companion object: Logging(OfferService::class.java)

    fun getAllOffers(): List<Offer> = offerRepo.findAll().map { it.toModel() }

    fun getOffer(offerId: Long): Offer? = offerRepo.findByIdOrNull(offerId)?.toModel()
}

@Service
class SubscriptionService(
    private val offerRepo: OfferRepository,
    private val subRepo: SubscriptionRepository,
) {
    companion object: Logging(SubscriptionService::class.java)

    fun subscribe(userId: Long, offerId: Long): Subscription? = runCatching {
        val offer = offerRepo.findByIdOrNull(offerId) ?: throw IllegalArgumentException("no offer with id $offerId")
        val subEntity = SubscriptionEntity(
            userId = userId,
            courseId = offer.courseId,
            duration = offer.duration,
            priceInCents = offer.priceInCents,
            startDate = LocalDate.now(),
        )
        subRepo.save(subEntity)
        subEntity
    }.getOrElse {
        logger.warn("couldn't created subscription for userId $userId and offerId $offerId", it)
        null
    }?.toModel()

    fun getSubscription(subId: Long): Subscription? = subRepo.findByIdOrNull(subId)?.toModel()

    fun pauseUnpauseSubscription(subId: Long): Subscription? = runCatching {
        subRepo.findByIdOrNull(subId)?.let{ subEntity ->
            when(subEntity.state) {
                SubscriptionState.ACTIVE -> {
                    subEntity.pausedDate = LocalDate.now()
                }
                SubscriptionState.PAUSED -> {
                    val pauseDate = subEntity.pausedDate
                    checkNotNull(pauseDate) {"subscription with id ${subEntity.id} has no pauseDate even though its state is PAUSED"}
                    val daysPaused = pauseDate.until(LocalDate.now(), ChronoUnit.DAYS).toInt()
                    subEntity.daysPaused += daysPaused
                    subEntity.pausedDate = null
                }
                else -> {}
            }
            subRepo.save(subEntity)
        }
    }.getOrNull()?.toModel()

    fun cancelSubscription(subId: Long): Subscription? = runCatching {
        subRepo.findByIdOrNull(subId)?.let{ subEntity ->
            // TODO check if the state is EXPIRED because currently you can switch it to CANCELLED which is a bit strange
            subEntity.isCancelled = true
            subRepo.save(subEntity)
        }
    }.getOrNull()?.toModel()
}

@Service
class CourseService(
    private val courseRepo: CourseRepository,
) {
    fun getCourse(courseId: Long): Course? = courseRepo.findByIdOrNull(courseId)?.toModel()

    fun createCourse(name: String, instructorName: String, level: Level?) {
        courseRepo.save(
            CourseEntity(
                name = name,
                instructorName = instructorName,
                level = level?.toString(),
            )
        )
    }
}

@Service
class UserService(
    private val userRepo: UserRepository,
) {
    fun getUser(userId: Long): User? = userRepo.findByIdOrNull(userId)?.toModel()
}

private fun OfferEntity.toModel() = Offer(
    id = this.id!!,
    courseId = this.courseId,
    availableFrom = this.availableFrom,
    duration = this.duration.toDurationModel(),
    priceInCents = this.priceInCents,
)

private fun String.toDurationModel(): OfferDuration {
    val (multiplier, unit) = this.split(" ")
    return OfferDuration.with(
        unit = OfferDurationUnit.valueOf(unit),
        multiplier = multiplier.toLong()
    )
}

private fun SubscriptionEntity.toModel() = Subscription(
    id = this.id!!,
    userId = this.userId,
    courseId = this.courseId,
    state = this.state,
    duration = this.duration.toDurationModel(),
    priceInCents = this.priceInCents,
    startDate = this.startDate,
    daysPaused = this.daysPaused.toLong()
)

private val SubscriptionEntity.state get() = when {
    this.isCancelled -> SubscriptionState.CANCELLED
    this.pausedDate != null -> SubscriptionState.PAUSED
    LocalDate.now() > this.startDate.plus(this.duration.toDurationModel()) -> SubscriptionState.EXPIRED
    else -> SubscriptionState.ACTIVE
}

private fun CourseEntity.toModel() = Course(
    id = this.id!!,
    name = this.name,
    instructorName = this.instructorName,
    level = this.level?.let { Level.valueOf(it) },
)

private fun UserEntity.toModel() = User(
    id = this.id!!,
    name = this.name,
)

fun LocalDate.plus(duration: OfferDuration): LocalDate = when(duration.unit) {
    OfferDurationUnit.DAYS -> this.plusDays(duration.multiplier)
    OfferDurationUnit.WEEKS -> this.plusWeeks(duration.multiplier)
    OfferDurationUnit.MONTHS -> this.plusMonths(duration.multiplier)
}