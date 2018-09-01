package marshi.graphqlsample.dataloader

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import marshi.graphqlsample.type.Entry
import org.dataloader.BatchLoader
import java.util.concurrent.CompletableFuture

class EntryBatchLoader {

  companion object {

    fun batchLoader(): BatchLoader<Long, List<Entry>> {
      val batchLoader = BatchLoader { keys: List<Long> ->
        CompletableFuture.supplyAsync { latestEntryList(keys) }
      }
      return batchLoader
    }

    private fun latestEntryList(ids: List<Long>): List<List<Entry>> {
      return mapOf(
          1L to listOf(
              Entry(100L, "title100", "text100"),
              Entry(101L, "title101", "text101"),
              Entry(102L, "title102", "text102"),
              Entry(103L, "title103", "text103")
          ),
          2L to listOf(
              Entry(200L, "title200", "text200"),
              Entry(201L, "title201", "text201"),
              Entry(202L, "title202", "text202")
          ),
          3L to listOf(
              Entry(300L, "title300", "text300"),
              Entry(301L, "title301", "text301"),
              Entry(302L, "title302", "text302")
          ),
          4L to listOf(
              Entry(400L, "title400", "text400"),
              Entry(401L, "title401", "text401")
          ),
          5L to listOf(
              Entry(500L, "title500", "text500"),
              Entry(501L, "title501", "text501"),
              Entry(502L, "title502", "text502")
          )
      ).filter { ids.contains(it.key) }
          .values.toList()
    }
  }
}
