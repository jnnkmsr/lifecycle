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

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

/**
 * Converts the given *cold* [flow] into a *hot* [StateFlow] that has its value
 * stored in the saved-state registry.
 *
 * @param flow The cold [Flow] to be converted.
 * @param initialValue The initial value of the [StateFlow].
 *   This value is also used when the state flow is reset using the
 *   [SharingStarted.WhileSubscribed] strategy with the `replayExpirationMillis`
 *   parameter.
 * @param scope The [CoroutineScope] in which sharing is started.
 * @param started The strategy that controls when sharing is started and stopped.
 *
 * @return A [PropertyDelegateProvider] that provides the [StateFlow] as a
 *   [ReadOnlyProperty].
 */
public fun <T> SavedStateHandle.savedStateFlow(
    flow: Flow<T>,
    initialValue: T,
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, StateFlow<T>>> =
    PropertyDelegateProvider { _, property ->
        val key = property.name
        val value = flow
            .onEach { value -> set(key, value) }
            .stateIn(
                scope = scope,
                started = started,
                initialValue = get(key) ?: initialValue
            )
        ReadOnlyProperty { _, _ -> value }
    }
