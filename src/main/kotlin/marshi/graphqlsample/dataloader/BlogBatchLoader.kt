package marshi.graphqlsample.dataloader

import marshi.graphqlsample.type.Blog
import org.dataloader.BatchLoader
import java.util.concurrent.CompletableFuture

class BlogBatchLoader {

  companion object {

    fun batchLoader(): BatchLoader<Long, Blog> {
      val batchLoader = BatchLoader { keys: List<Long> ->
        CompletableFuture.supplyAsync { bloggerList(keys) }
      }
      return batchLoader
    }

    private fun bloggerList(ids: List<Long>): List<Blog> {
      return listOf(
          Blog(1L, "AA"),
          Blog(2L, "BB"),
          Blog(3L, "CC"),
          Blog(4L, "DD"),
          Blog(5L, "EE")
      ).filter { ids.contains(it.id) }
    }
  }
}
