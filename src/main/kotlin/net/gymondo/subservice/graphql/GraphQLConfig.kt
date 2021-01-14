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
import net.gymondo.subservice.ResourceLoader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration


@Configuration
class GraphQLProvider(
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
        }
    }.build()

}

private inline fun <reified A:Any?, O> dataFetcherByArgument(
    argumentName: String,
    crossinline transform: (A)->O
): DataFetcher<O> =  DataFetcher<O> { env: DataFetchingEnvironment ->
    val argument: A = env.getArgument(argumentName)
    transform(argument)
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
