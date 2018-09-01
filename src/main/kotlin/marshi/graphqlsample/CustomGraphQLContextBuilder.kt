package marshi.graphqlsample

import marshi.graphqlsample.dataloader.BlogBatchLoader
import marshi.graphqlsample.dataloader.EntryBatchLoader
import graphql.servlet.GraphQLContext
import graphql.servlet.GraphQLContextBuilder
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.dataloader.DataLoaderRegistry
import org.dataloader.stats.SimpleStatisticsCollector
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.websocket.server.HandshakeRequest

@Component
class CustomGraphQLContextBuilder : GraphQLContextBuilder {

  override fun build(httpServletRequest: HttpServletRequest): GraphQLContext {
    val graphQLContext = GraphQLContext(httpServletRequest)
    graphQLContext.setDataLoaderRegistry(this.registries())
    return graphQLContext
  }

  override fun build(handshakeRequest: HandshakeRequest): GraphQLContext {
    return GraphQLContext(handshakeRequest)
  }

  override fun build(): GraphQLContext {
    return GraphQLContext()
  }

  private fun registries(): DataLoaderRegistry {
    val options = options()
    val registry = DataLoaderRegistry()

    val blogDataLoader = DataLoader(BlogBatchLoader.batchLoader(), options)
    registry.register("blogs", blogDataLoader)
    val entryDataLoader = DataLoader(EntryBatchLoader.batchLoader(), options)
    registry.register("entries", entryDataLoader)
    return registry
  }

  private fun options(): DataLoaderOptions {
    val options = DataLoaderOptions.newOptions()
    options.setStatisticsCollector{SimpleStatisticsCollector()}
    return options
  }
}
