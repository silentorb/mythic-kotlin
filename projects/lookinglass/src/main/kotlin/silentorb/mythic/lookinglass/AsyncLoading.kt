package silentorb.mythic.lookinglass

import kotlin.concurrent.thread

typealias DynamicAssetLibrary<T> = MutableMap<String, T>
typealias LoadDeferred<Deferred, Loaded> = (List<Deferred>) -> List<Loaded>
typealias FinalizeLoaded<Loaded, Asset> = (List<Loaded>) -> Map<String, Asset>

data class LoadingState<Deferred, Loaded>(
    var remaining: List<Deferred>,
    var batchSize: Int = 8,
    var loadedBuffer: List<Loaded>? = null,
    var isLoading: Boolean = false
)

fun <Deferred, Loaded, Asset> updateAsyncLoading(
    loadDeferred: LoadDeferred<Deferred, Loaded>,
    finalizeLoaded: FinalizeLoaded<Loaded, Asset>): (LoadingState<Deferred, Loaded>, DynamicAssetLibrary<Asset>) -> Unit =
    { state, destination ->
      if (!state.isLoading) {
        state.isLoading = true
        val loaded = state.loadedBuffer
        if (loaded != null) {
          destination += finalizeLoaded(loaded)
          state.loadedBuffer = null
        }
        if (state.remaining.any()) {
          val next = state.remaining.take(state.batchSize)
          state.remaining = state.remaining.drop(state.batchSize)

          thread(start = true) {
            state.loadedBuffer = loadDeferred(next)
            state.isLoading = false
          }
        }
      }
    }
