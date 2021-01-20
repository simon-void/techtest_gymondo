package net.gymondo.subservice.repository

import org.springframework.data.repository.CrudRepository


interface SubscriptionRepository : CrudRepository<SubscriptionEntity, Long>

interface UserRepository : CrudRepository<UserEntity, Long>

interface OfferRepository : CrudRepository<OfferEntity, Long>

interface CourseRepository : CrudRepository<CourseEntity, Long>
