package net.gymondo.subservice

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import net.gymondo.subservice.repository.OfferRepository
import net.gymondo.subservice.repository.SubscriptionEntity
import net.gymondo.subservice.repository.SubscriptionRepository
import net.gymondo.subservice.service.*
import org.junit.Assert
import org.springframework.data.repository.findByIdOrNull
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.time.LocalDate

class SubscriptionServiceTests {

    /*
     * Normalerweise würde ich mehr als einen Test schreiben.
     * Der Test dient als nicht komplett trivales Beispiel einer Testkonfiguration.
     */

    private val offerRepo: OfferRepository = mockk()
    private val subRepo: SubscriptionRepository = mockk()
    private val subService = SubscriptionService(offerRepo, subRepo)

    @BeforeMethod
    fun setup() {
        clearMocks(offerRepo, subRepo)
    }

    @Test(dataProvider = "getManualConfigData")
    fun `test if the entityMapping in getSubscription works`(
        providedEntity: SubscriptionEntity?,
        expectedSub: Subscription?
    ) {
        // init mocks
        every { subRepo.findByIdOrNull(any()) }.returns(providedEntity)

        // execute test
        val actualSub = subService.getSubscription(providedEntity?.id ?: 1L)

        // compare expected with actual subscription
        Assert.assertEquals("subscriptions", expectedSub, actualSub)
    }

    @DataProvider
    fun getManualConfigData(): Array<Array<Any?>> {
        val today = LocalDate.now()
        val oneDayAgo = today.minusDays(1)
        val twoDaysAgo = today.minusDays(2)

        return arrayOf(
            arrayOf<Any?>(null, null),
            arrayOf<Any?>(
                SubscriptionEntity(
                    userId = 1L,
                    courseId = 2L,
                    duration = OfferDuration.with(OfferDurationUnit.DAYS, 3).toString(),
                    priceInCents = 123,
                    startDate = today,
                    pausedDate = null,
                    daysPaused = 0,
                    isCancelled = false,
                    id = 5L,
                ),
                Subscription(
                    id = 5L,
                    userId = 1L,
                    courseId = 2L,
                    state = SubscriptionState.ACTIVE,
                    duration = OfferDuration.with(OfferDurationUnit.DAYS, 3),
                    priceInCents = 123,
                    startDate = today,
                    daysPaused = 0,
                )
            ),
            arrayOf<Any?>(
                SubscriptionEntity(
                    userId = 1L,
                    courseId = 2L,
                    duration = OfferDuration.with(OfferDurationUnit.DAYS, 4).toString(),
                    priceInCents = 124,
                    startDate = twoDaysAgo,
                    pausedDate = oneDayAgo,
                    daysPaused = 4,
                    isCancelled = false,
                    id = 5L,
                ),
                Subscription(
                    id = 5L,
                    userId = 1L,
                    courseId = 2L,
                    state = SubscriptionState.PAUSED,
                    duration = OfferDuration.with(OfferDurationUnit.DAYS, 4),
                    priceInCents = 124,
                    startDate = twoDaysAgo,
                    daysPaused = 4,
                )
            ),
            arrayOf<Any?>(
                SubscriptionEntity(
                    userId = 1L,
                    courseId = 2L,
                    duration = OfferDuration.with(OfferDurationUnit.DAYS, 3).toString(),
                    priceInCents = 123,
                    startDate = today,
                    pausedDate = null,
                    daysPaused = 0,
                    isCancelled = true,
                    id = 5L,
                ),
                Subscription(
                    id = 5L,
                    userId = 1L,
                    courseId = 2L,
                    state = SubscriptionState.CANCELLED,
                    duration = OfferDuration.with(OfferDurationUnit.DAYS, 3),
                    priceInCents = 123,
                    startDate = today,
                    daysPaused = 0,
                )
            ),
            arrayOf<Any?>(
                SubscriptionEntity(
                    userId = 1L,
                    courseId = 2L,
                    duration = OfferDuration.with(OfferDurationUnit.DAYS, 1).toString(),
                    priceInCents = 123,
                    startDate = twoDaysAgo,
                    pausedDate = null,
                    daysPaused = 0,
                    isCancelled = false,
                    id = 5L,
                ),
                Subscription(
                    id = 5L,
                    userId = 1L,
                    courseId = 2L,
                    state = SubscriptionState.EXPIRED,
                    duration = OfferDuration.with(OfferDurationUnit.DAYS, 1),
                    priceInCents = 123,
                    startDate = twoDaysAgo,
                    daysPaused = 0,
                )
            ),
        )
    }
}
