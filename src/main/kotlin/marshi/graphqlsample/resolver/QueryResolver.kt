package marshi.graphqlsample.resolver

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import marshi.graphqlsample.resolver.field.BlogListResponseResolver
import org.springframework.stereotype.Component

@Component
class QueryResolver : GraphQLQueryResolver{

  fun bloginfo(ids: List<Long>) = BlogListResponseResolver(ids)

}
