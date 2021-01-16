package net.gymondo.subservice.service

import java.time.LocalDate
import java.util.*


data class Subscription(
    val id: UUID,
    val userId: UUID,
    val courseId: UUID,
    val duration: OfferDuration,
    val priceInCents: Int,
    val startDate: LocalDate,
) {
    val endDate: LocalDate get() = when(duration.unit) {
        OfferDurationUnit.DAYS -> startDate.plusDays(duration.multiplier)
        OfferDurationUnit.WEEKS -> startDate.plusWeeks(duration.multiplier)
        OfferDurationUnit.MONTHS -> startDate.plusMonths(duration.multiplier)
    }
}

data class User(
    val id: UUID,
    val name: String,
    val passwordHash: String,
)

data class Offer(
    val id: UUID,
    val courseId: UUID,
    val availableFrom: LocalDate,
    val availableUntil: LocalDate,
    val duration: OfferDuration,
    val priceInCents: Int,
)

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
    val id: UUID,
    val name: String,
    val instructorId: UUID,
    val level: Level,
    val occurrences: List<Occurrence>, // assume I also checked that occurrences aren't overlapping
)

data class Instructor (
    val id: UUID,
    val firstName: String,
    val lastName: String,
) {
    val fullName: String get() = "$firstName $lastName"
}

enum class Level {
    BEGINNER, INTERMEDIATE, ADVANCED
}

data class Occurrence private constructor(
    val weekday: Weekday,
    val startTime: TimePoint,
    val endTime: TimePoint,
) {
    companion object {
        fun with(weekday: Weekday, startTime: TimePoint, endTime: TimePoint): Occurrence {
            require(startTime<endTime) {"start time $startTime isn't smaller then end time $endTime"}
            return Occurrence(weekday, startTime, endTime)
        }
    }
}

enum class Weekday {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

data class TimePoint private constructor(
    val hour: Int,
    val minute: Int,
) : Comparable<TimePoint> {
    companion object {
        fun with(hour: Int, minute: Int = 0): TimePoint {
            require(hour in 0..23) {"hour should be in 0-23 but is $hour"}
            require(minute in 0..59) {"hour should be in 0-59 but is $minute"}
            return TimePoint(hour, minute)
        }
    }

    override fun toString(): String {
        fun toTwoDigits(value: Int): String = if(value<10) {
            "0$value"
        } else {
            "$value"
        }
        return "${toTwoDigits(hour)}:${toTwoDigits(minute)}"
    }

    override fun compareTo(other: TimePoint): Int = when {
        hour < other.hour -> -1
        hour > other.hour -> 1
        minute < other.minute -> -1
        minute > other.minute -> 1
        else -> 0
    }
}
