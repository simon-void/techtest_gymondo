package net.gymondo.subservice.repository

import net.gymondo.subservice.service.OfferDuration
import net.gymondo.subservice.service.OfferDurationUnit
import java.time.LocalDate
import java.util.*
import javax.persistence.*

import javax.persistence.GeneratedValue


@Entity
class SubscriptionEntity(
    var userId: Long,
    var courseId: Long,
    var duration: String,
    var priceInCents: Int,
    var startDate: LocalDate,
    @Id @GeneratedValue var id: Long? = null,
) {
    constructor() : this(0L, 0L, "", -1, LocalDate.MIN)

    override fun toString() = "SubscriptionEntity(id=$id, userId=$userId, courseId=$courseId, dur=$duration, price=$priceInCents, start=$startDate)"

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null || javaClass != other.javaClass -> false
        other !is SubscriptionEntity -> false
        else -> id == other.id
    }

    override fun hashCode(): Int = id?.toInt() ?: 0
}

@Entity
class UserEntity(
    var name: String,
    var passwordHash: String,
    @Id @GeneratedValue var id: Long? = null,
) {
    constructor() : this("", "")

    override fun toString() = "UserEntity(id=$id, userId=$name, passwordHash=...)"

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null || javaClass != other.javaClass -> false
        other !is UserEntity -> false
        else -> id == other.id
    }

    override fun hashCode(): Int = id?.toInt() ?: 0
}

@Entity
class OfferEntity(
    var courseId: Long,
    var duration: String,
    var availableFrom: LocalDate,
    var availableTo: LocalDate,
    var priceInCents: Int,
    @Id @GeneratedValue var id: Long? = null,
) {
    constructor() : this(0L, "", LocalDate.MIN, LocalDate.MIN, -1)

    override fun toString() = "OfferEntity(id=$id, courseId=$courseId, dur=$duration, from=$availableFrom, to=$availableTo, price=$priceInCents)"

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null || javaClass != other.javaClass -> false
        other !is OfferEntity -> false
        else -> id == other.id
    }

    override fun hashCode(): Int = id?.toInt() ?: 0
}
