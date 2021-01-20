package net.gymondo.subservice.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDate


interface SubscriptionRepository : CrudRepository<SubscriptionEntity, Long>

interface UserRepository : CrudRepository<UserEntity, Long>

interface OfferRepository : CrudRepository<OfferEntity, Long> {

    @Query("SELECT e.* FROM OfferEntity WHERE DATE(availableFrom) <= :today AND DATE(availableTo) >= :today", nativeQuery = true)
    fun findAllActive(today: LocalDate): List<OfferEntity>
}

interface CourseRepository : CrudRepository<CourseEntity, Long>
