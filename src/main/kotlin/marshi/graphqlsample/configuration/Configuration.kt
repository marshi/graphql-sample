package marshi.graphqlsample.configuration

import graphql.servlet.GraphQLQueryInvoker
import marshi.graphqlsample.ExtendedGraphQLInvoker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration {

  @Bean
  fun queryInvoker(): GraphQLQueryInvoker = ExtendedGraphQLInvoker()

}
