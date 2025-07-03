package merail.life.core.extensions

import kotlin.coroutines.cancellation.CancellationException

inline fun <R> suspendableRunCatching(
    block: () -> R,
): Result<R> = try {
    Result.success(block())
} catch (c: CancellationException) {
    throw c
} catch (e: Throwable) {
    Result.failure(e)
}