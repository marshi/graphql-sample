package marshi.graphqlsample.resolver.field

import com.coxautodev.graphql.tools.GraphQLResolver
import marshi.graphqlsample.type.Blog
import marshi.graphqlsample.type.BlogListResponse
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import java.util.concurrent.CompletableFuture

class BlogListResponseResolver(private val ids: List<Long>) : GraphQLResolver<BlogListResponse> {

  fun blogs(env: DataFetchingEnvironment): CompletableFuture<List<Blog>> {
    val context = env.getContext<GraphQLContext>()
    val registry = context.dataLoaderRegistry.get()
    val dataLoader = registry.getDataLoader<Long, Blog>("blogs")
    return dataLoader.loadMany(ids)
  }

}