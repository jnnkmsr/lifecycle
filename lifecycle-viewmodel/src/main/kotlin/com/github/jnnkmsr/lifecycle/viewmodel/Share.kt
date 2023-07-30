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

package com.github.jnnkmsr.lifecycle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Converts the given _cold_ [flow] into a _hot_ [SharedFlow] that is started
 * in `this` [ViewModel]'s [viewModelScope]. Delegates to [shareIn] using the
 * [viewModelScope] together with the given [context].
 *
 * @param started The strategy that controls when sharing is started and stopped.
 * @param context [CoroutineContext] that is added to the [viewModelScope]
 *   in which sharing is started. Defaults to [EmptyCoroutineContext].
 * @param replay The number values replayed to new subscribers (cannot be
 *   negative; defaults to zero).
 *
 * @see shareIn
 */
public fun <T> ViewModel.share(
    flow: Flow<T>,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    replay: Int = 0,
): SharedFlow<T> = flow.shareIn(
    scope = viewModelScope + context,
    started = started,
    replay = replay,
)

/**
 * Converts the given _cold_ [flow] into a _hot_ [StateFlow] that is started
 * in `this` [ViewModel]'s [viewModelScope]. Delegates to [stateIn] using the
 * [viewModelScope] together with the given [context].
 *
 * @param initialValue The initial value of the [StateFlow]. This value is
 *   also used when the state flow is reset using the
 *   [SharingStarted.WhileSubscribed] strategy with the
 *   `replayExpirationMillis` parameter.
 * @param started The strategy that controls when sharing is started and stopped.
 * @param context [CoroutineContext] that is added to the [viewModelScope]
 *   in which sharing is started. Defaults to [EmptyCoroutineContext].
 *
 * @see stateIn
 */
public fun <T> ViewModel.state(
    flow: Flow<T>,
    initialValue: T,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
): StateFlow<T> = flow.stateIn(
    scope = viewModelScope + context,
    started = started,
    initialValue = initialValue,
)
