package marshi.graphqlsample

import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions
import graphql.execution.preparsed.NoOpPreparsedDocumentProvider
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.servlet.DefaultExecutionStrategyProvider
import graphql.servlet.ExecutionStrategyProvider
import graphql.servlet.GraphQLContext
import graphql.servlet.GraphQLQueryInvoker
import java.util.*
import java.util.function.Supplier

/**
 * dataloader statisticsを有効化するためにgetInstrumentationをoverride.
 * graphql-java-servlet内で
 * instrumentations.add(DataLoaderDispatcherInstrumentation(registry)
 * されているが、dataloader statisticsを有効化するためには第二引数が渡されている必要がある.
 * しかし、その方法が提供されていないように思われるので無理やりoverrideして解決している.
 */
class ExtendedGraphQLInvoker(
    getExecutionStrategyProvider: Supplier<ExecutionStrategyProvider> = Supplier { DefaultExecutionStrategyProvider() },
    private val getInstrumentation: Supplier<Instrumentation> = Supplier { SimpleInstrumentation.INSTANCE },
    getPreparsedDocumentProvider: Supplier<PreparsedDocumentProvider> = Supplier { NoOpPreparsedDocumentProvider.INSTANCE }
) : GraphQLQueryInvoker(
    getExecutionStrategyProvider,
    getInstrumentation,
    getPreparsedDocumentProvider
) {

  override fun getInstrumentation(context: Any?): Instrumentation {
    return if (context is GraphQLContext) {
      context.dataLoaderRegistry
          .map { registry ->
            val instrumentations = ArrayList<Instrumentation>()
            instrumentations.add(getInstrumentation.get())
            instrumentations.add(DataLoaderDispatcherInstrumentation(registry, DataLoaderDispatcherInstrumentationOptions.newOptions().includeStatistics(true)))
            ChainedInstrumentation(instrumentations)
          }
          .map { it as Instrumentation }
          .orElse(getInstrumentation.get())
    } else getInstrumentation.get()
  }
}
