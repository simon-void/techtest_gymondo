package net.gymondo.subservice.service

import java.time.LocalDate


data class Subscription(
    val id: Long,
    val userId: Long,
    val courseId: Long,
    val state: SubscriptionState,
    val duration: OfferDuration,
    val priceInCents: Int,
    val startDate: LocalDate,
) {
    // I'll have to add the paused days
    val endDate: LocalDate get() = when(duration.unit) {
        OfferDurationUnit.DAYS -> startDate.plusDays(duration.multiplier)
        OfferDurationUnit.WEEKS -> startDate.plusWeeks(duration.multiplier)
        OfferDurationUnit.MONTHS -> startDate.plusMonths(duration.multiplier)
    }
}

enum class SubscriptionState {
    ACTIVE, CANCELLED, EXPIRED
}

data class User(
    val id: Long,
    val name: String,
)

data class Offer(
    val id: Long,
    val courseId: Long,
    val availableFrom: LocalDate,
    val duration: OfferDuration,
    val priceInCents: Int,
) {
    val availableUntil: LocalDate get() = availableFrom.plus(duration)
}

data class OfferDuration private constructor(
    val unit: OfferDurationUnit,
    val multiplier: Long,
) {
    override fun toString() = "$multiplier $unit"

    companion object {
        fun with(unit: OfferDurationUnit, multiplier: Long): OfferDuration {
            require(multiplier>0) {"multiplier has to be bigger than 0 but is $multiplier"}
            return OfferDuration(unit, multiplier)
        }
    }
}

enum class OfferDurationUnit {
    DAYS, WEEKS, MONTHS
}

data class Course(
    val id: Long,
    val name: String,
    val level: Level?,
    val instructorName: String,
    // in a real project, I'd choose to have an instructor entity and a localizable schedule
    //val schedule: List<Occurrence>,
)

enum class Level {
    BEGINNER, INTERMEDIATE, ADVANCED
}
