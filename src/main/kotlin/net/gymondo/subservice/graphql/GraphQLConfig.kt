package net.gymondo.subservice.graphql

import graphql.GraphQL
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import org.springframework.context.annotation.Bean

import graphql.schema.idl.TypeRuntimeWiring.newTypeWiring
import net.gymondo.subservice.LocalizationProperties
import net.gymondo.subservice.ResourceLoader
import net.gymondo.subservice.service.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration


@Configuration
class GraphQLProvider(
    private val localizationProperties: LocalizationProperties,
    private val offerService: OfferService,
    private val subService: SubscriptionService,
    private val courseService: CourseService,
    private val userService: UserService,
) {

    @Bean
    fun graphQL(@Qualifier("utf8ResourceLoader") resourceLoader: ResourceLoader): GraphQL {
        val sdl = resourceLoader.load("schema.graphqls")
        val graphQLSchema = SchemaGenerator().makeExecutableSchema(
                SchemaParser().parse(sdl),
                buildWiring()
        )
        return GraphQL.newGraphQL(graphQLSchema).build()
    }

    private fun buildWiring(): RuntimeWiring = RuntimeWiring.newRuntimeWiring().apply {

        addTypeWiring("Query") {
            register("echo", dataFetcherByArgument("msg") { msg: String -> msg })
            register("products") {
                offerService.getAllOffers()
            }
            register("product", dataFetcherByArgument("offerId") { offerId: String ->
                offerService.getOffer(offerId.toLong())
            })
            register( "buy", dataFetcherByTwoArguments("userId", "offerId") {userId: String, offerId: String ->
                subService.subscribe(userId.toLong(), offerId.toLong())
            })
            register("subscription", dataFetcherByArgument("subId") { subId: String ->
                subService.getSubscription(subId.toLong())
            })
            register("cancelSubscription", dataFetcherByArgument("subId") { subId: String ->
                subService.cancelSubscription(subId.toLong())
            })
        }

        addTypeWiring("Offer") {
            register("course", dataFetcherBySource { offer: Offer ->
                courseService.getCourse(offer.courseId)
            })
            register("availableFrom", dataFetcherBySource { offer: Offer ->
                offer.availableFrom.toString()
            })
            register("availableTo", dataFetcherBySource { offer: Offer ->
                offer.availableUntil.toString()
            })
        }

        addTypeWiring("Subscription") {
            register("course", dataFetcherBySource { sub: Subscription ->
                courseService.getCourse(sub.courseId)
            })
            register("user", dataFetcherBySource { sub: Subscription ->
                userService.getUser(sub.userId)
            })
            register("startDate", dataFetcherBySource { sub: Subscription ->
                sub.startDate.toString()
            })
            register("endDate", dataFetcherBySource { sub: Subscription ->
                sub.endDate.toString()
            })
            register("taxInCents", dataFetcherBySource { sub: Subscription ->
                (sub.priceInCents * localizationProperties.taxPercentage).toInt()
            })
        }

//        addTypeWiring("OfferDuration") {
//
//        }

//        addTypeWiring("Course") {
//        }
    }.build()

}

private inline fun <reified A:Any?, O> dataFetcherByArgument(
    argumentName: String,
    crossinline transform: (A)->O
): DataFetcher<O> =  DataFetcher<O> { env: DataFetchingEnvironment ->
    val argument: A = env.getArgument(argumentName)
    transform(argument)
}

private inline fun <reified A:Any?, B:Any?, O> dataFetcherByTwoArguments(
    argumentName1: String,
    argumentName2: String,
    crossinline transform: (A, B)->O
): DataFetcher<O> =  DataFetcher<O> { env: DataFetchingEnvironment ->
    val argument1: A = env.getArgument(argumentName1)
    val argument2: B = env.getArgument(argumentName2)
    transform(argument1, argument2)
}

private inline fun <reified S:Any, O> dataFetcherBySource(
    crossinline transform: (S)->O
): DataFetcher<O> =  DataFetcher<O> { env: DataFetchingEnvironment ->
    val source: S = env.getSource()
    transform(source)
}

private fun RuntimeWiring.Builder.addTypeWiring(graphQLType: String, executeOnContext: TypeWiringContext.()->Unit) {
    val builder: TypeRuntimeWiring.Builder = newTypeWiring(graphQLType)
    val context = TypeWiringContext(builder)
    context.executeOnContext()
    this.type(builder)
}

private class TypeWiringContext(private val builder: TypeRuntimeWiring.Builder) {
    fun register(fieldName: String, dataFetcher: DataFetcher<*>) {
        builder.dataFetcher(fieldName, dataFetcher)
    }
}
