package com.seiko.compose.player.internal

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce

internal class DebounceFlow<T>(timeoutMillis: Long) : Flow<T> {

  private val channel = Channel<T>(capacity = 1)

  fun send(item: T) {
    channel.trySend(item)
  }

  @OptIn(FlowPreview::class)
  private val flow: Flow<T> = channel.consumeAsFlow()
    .debounce(timeoutMillis)

  @InternalCoroutinesApi
  override suspend fun collect(collector: FlowCollector<T>) {
    flow.collect(collector)
  }
}