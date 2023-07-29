/*
 * Copyright 2023 Jannik Möser
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jnnkmsr.lifecycle.flow

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Collects values from this [StateFlow] and represents its latest value via
 * [State] in a lifecycle-aware manner.
 *
 * The [StateFlow.value] is used as an initial value. Every time there would be
 * a new value posted into the [StateFlow], the returned [State] will be updated
 * causing recomposition of every [State.value] usage whenever the [lifecycleOwner]'s
 * lifecycle is at least [minActiveState].
 *
 * This [StateFlow] is collected every time the [lifecycleOwner]'s lifecycle
 * reaches the [minActiveState] lifecycle state. The collection stops when the
 * [lifecycleOwner]'s lifecycle falls below [minActiveState].
 *
 * @param lifecycleOwner The [LifecycleOwner] whose `lifecycle` is used to
 *   restart collecting `this` flow.
 * @param minActiveState The [Lifecycle.State] in which the upstream flow gets
 *   collected. The collection will stop if the lifecycle falls below that
 *   state, and will restart if it's in that state again.
 * @param context The [CoroutineContext] to use for collecting.
 */
public fun <T> StateFlow<T>.collectAsStateWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = collectAsStateWithLifecycle(
    initialValue = value,
    lifecycleOwner = lifecycleOwner,
    minActiveState = minActiveState,
    context = context,
)

/**
 * Collects values from this [Flow] and represents its latest value via [State]
 * in a lifecycle-aware manner.
 *
 * Every time there would be a new value posted into the [Flow], the returned
 * [State] will be updated causing recomposition of every [State.value] usage
 * whenever the [lifecycleOwner]'s lifecycle is at least [minActiveState].
 *
 * This [Flow] is collected every time the [lifecycleOwner]'s lifecycle reaches
 * the [minActiveState] lifecycle state. The collection stops when the
 * [lifecycleOwner]'s lifecycle falls below [minActiveState].
 *
 * @param initialValue The [State]'s initial value.
 * @param lifecycleOwner The [LifecycleOwner] whose `lifecycle` is used to
 *   restart collecting `this` flow.
 * @param minActiveState The [Lifecycle.State] in which the upstream flow gets
 *   collected. The collection will stop if the lifecycle falls below that
 *   state, and will restart if it's in that state again.
 * @param context The [CoroutineContext] to use for collecting.
 */
public fun <T> Flow<T>.collectAsStateWithLifecycle(
    initialValue: T,
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> {
    val state = mutableStateOf(initialValue)

    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            withContextIfNotEmpty(context) {
                this@collectAsStateWithLifecycle
                    .onEach { state.value = it }
                    .collect()
            }
        }
    }

    return state
}

/**
 * Runs the [action] in a [withContext] block, if the [context] is not an
 * [EmptyCoroutineContext].
 */
private suspend inline fun withContextIfNotEmpty(
    context: CoroutineContext,
    crossinline action: suspend () -> Unit,
) {
    if (context == EmptyCoroutineContext) {
        action()
    } else {
        withContext(context) { action() }
    }
}
