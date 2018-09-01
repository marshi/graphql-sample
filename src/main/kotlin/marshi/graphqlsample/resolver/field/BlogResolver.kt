package marshi.graphqlsample.resolver.field

import com.coxautodev.graphql.tools.GraphQLResolver
import marshi.graphqlsample.type.Blog
import marshi.graphqlsample.type.Entry
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class BlogResolver : GraphQLResolver<Blog> {

  fun entries(blog: Blog, env: DataFetchingEnvironment): CompletableFuture<List<Entry>> {
    val context = env.getContext<GraphQLContext>()
    val registry = context.dataLoaderRegistry.get()
    val dataLoader = registry.getDataLoader<Long, List<Entry>>("entries")
    return dataLoader.load(blog.id)
  }
}
