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

import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jnnkmsr.lifecycle.flow.MutableSavedStateFlow
import com.github.jnnkmsr.lifecycle.flow.mutableSavedStateFlow
import com.github.jnnkmsr.lifecycle.flow.savedStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

/**
 * A [ViewModel] that has access to the saved-state registry via a
 * [SavedStateHandle].
 */
public abstract class SavedStateViewModel : ViewModel() {

    /**
     * The [SavedStateHandle] instance, typically, obtained by constructor
     * injection.
     */
    @get:RestrictTo(RestrictTo.Scope.SUBCLASSES)
    public abstract val savedStateHandle: SavedStateHandle

    /**
     * Constructs a [MutableSavedStateFlow] that is stored in the saved-state registry.
     *
     * @param initialValue The initial value of the state flow.
     *
     * @return A [PropertyDelegateProvider] that provides the [MutableSavedStateFlow]
     *   as a [ReadOnlyProperty].
     */
    public fun <T> mutableSavedStateFlow(
        initialValue: T,
    ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, MutableSavedStateFlow<T>>> =
        savedStateHandle.mutableSavedStateFlow(initialValue)

    /**
     * Converts the given *cold* [flow] into a *hot* [StateFlow] that has its
     * value stored in the saved-state registry.
     *
     * @param flow The cold [Flow] to be converted.
     * @param initialValue The initial value of the [StateFlow].
     *   This value is also used when the state flow is reset using the
     *   [SharingStarted.WhileSubscribed] strategy with the
     *   `replayExpirationMillis` parameter.
     * @param started The strategy that controls when sharing is started and
     *   stopped.
     * @param context [CoroutineContext] that is added to the [viewModelScope]
     *   in which sharing is started. Defaults to [EmptyCoroutineContext].
     *
     * @return A [PropertyDelegateProvider] that provides the [StateFlow] as a
     *   [ReadOnlyProperty].
     */
    public fun <T> savedStateFlow(
        flow: Flow<T>,
        initialValue: T,
        started: SharingStarted = SharingStarted.WhileSubscribed(),
        context: CoroutineContext = EmptyCoroutineContext,
    ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, StateFlow<T>>> =
        savedStateHandle.savedStateFlow(
            flow = flow,
            initialValue = initialValue,
            scope = viewModelScope + context,
            started = started,
        )

    /**
     * Transforms the given *cold* [flow] using the given [transform] function
     * and converts the result into a *hot* [StateFlow] that has its value
     * stored in the saved-state registry.
     *
     * @param flow The cold [Flow] to be converted.
     * @param initialValue The initial value of the [StateFlow]. This value is
     *   also used when the state flow is reset using the
     *   [SharingStarted.WhileSubscribed] strategy with the
     *   `replayExpirationMillis` parameter.
     * @param started The strategy that controls when sharing is started and
     *   stopped.
     * @param context [CoroutineContext] that is added to the [viewModelScope]
     *   in which sharing is started. Defaults to [EmptyCoroutineContext].
     * @param transform The transform to apply to each value of the original [flow]
     *   before conversion to a [StateFlow].
     *
     * @return A [PropertyDelegateProvider] that provides the [StateFlow] as a
     *   [ReadOnlyProperty].
     */
    public inline fun <T, R> savedStateFlow(
        flow: Flow<T>,
        initialValue: R,
        started: SharingStarted = SharingStarted.WhileSubscribed(),
        context: CoroutineContext = EmptyCoroutineContext,
        crossinline transform: suspend (T) -> R,
    ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, StateFlow<R>>> =
        savedStateHandle.savedStateFlow(
            flow = flow.map(transform),
            initialValue = initialValue,
            scope = viewModelScope + context,
            started = started,
        )

    /**
     * Converts `this` _cold_ [Flow] into a _hot_ [SharedFlow] that is started
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
    public fun <T> Flow<T>.share(
        started: SharingStarted = SharingStarted.WhileSubscribed(),
        context: CoroutineContext = EmptyCoroutineContext,
        replay: Int = 0,
    ): SharedFlow<T> = share(this, started, context, replay)

    /**
     * Converts `this` _cold_ [Flow] into a _hot_ [StateFlow] that is started
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
    public fun <T> Flow<T>.state(
        initialValue: T,
        started: SharingStarted = SharingStarted.WhileSubscribed(),
        context: CoroutineContext = EmptyCoroutineContext,
    ): StateFlow<T> = state(this, initialValue, started, context)
}

/**
 * Combines the given *cold* [flows] using the given [transform] function and
 * converts the result into a *hot* [StateFlow] that has its value stored in the
 * saved-state registry.
 *
 * @param flows The cold [Flow]s to be combined.
 * @param initialValue The initial value of the [StateFlow].
 *   This value is also used when the state flow is reset using the
 *   [SharingStarted.WhileSubscribed] strategy with the `replayExpirationMillis`
 *   parameter.
 * @param started The strategy that controls when sharing is started and stopped.
 * @param context [CoroutineContext] that is added to the [viewModelScope] in
 *   which sharing is started. Defaults to [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows] before conversion to a [StateFlow].
 *
 * @return A [PropertyDelegateProvider] that provides the [StateFlow] as a
 *   [ReadOnlyProperty].
 */
public inline fun <reified T, R> SavedStateViewModel.savedStateFlow(
    flows: Iterable<Flow<T>>,
    initialValue: R,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline transform: suspend (Array<T>) -> R,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, StateFlow<R>>> = savedStateFlow(
    flow = combine(flows, transform),
    initialValue = initialValue,
    context = context,
    started = started,
)

/**
 * Combines the given *cold* [flows] using the given [transform] function and
 * converts the result into a *hot* [StateFlow] that has its value stored in the
 * saved-state registry.
 *
 * @param flows The cold [Flow]s to be combined.
 * @param initialValue The initial value of the [StateFlow].
 *   This value is also used when the state flow is reset using the
 *   [SharingStarted.WhileSubscribed] strategy with the `replayExpirationMillis`
 *   parameter.
 * @param started The strategy that controls when sharing is started and stopped.
 * @param context [CoroutineContext] that is added to the [viewModelScope] in
 *   which sharing is started. Defaults to [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows] before conversion to a [StateFlow].
 *
 * @return A [PropertyDelegateProvider] that provides the [StateFlow] as a
 *   [ReadOnlyProperty].
 */
public inline fun <reified T, R> SavedStateViewModel.savedStateFlow(
    vararg flows: Flow<T>,
    initialValue: R,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline transform: suspend (Array<T>) -> R,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, StateFlow<R>>> = savedStateFlow(
    flows = flows.toList(),
    initialValue = initialValue,
    context = context,
    started = started,
    transform = transform,
)

/**
 * Combines the given *cold* [flows][flow1]  using the given [transform] function
 * and converts the result into a *hot* [StateFlow] that has its value stored in
 * the saved-state registry.
 *
 * @param flow1 A cold [Flow] to be combined.
 * @param flow2 A cold [Flow] to be combined.
 * @param initialValue The initial value of the [StateFlow].
 *   This value is also used when the state flow is reset using the
 *   [SharingStarted.WhileSubscribed] strategy with the `replayExpirationMillis`
 *   parameter.
 * @param started The strategy that controls when sharing is started and stopped.
 * @param context [CoroutineContext] that is added to the [viewModelScope] in
 *   which sharing is started. Defaults to [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows][flow1] before conversion to a [StateFlow].
 *
 * @return A [PropertyDelegateProvider] that provides the [StateFlow] as a
 *   [ReadOnlyProperty].
 */
public fun <T1, T2, R> SavedStateViewModel.savedStateFlow(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    initialValue: R,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    transform: suspend (T1, T2) -> R,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, StateFlow<R>>> = savedStateFlow(
    flow = combine(flow1, flow2, transform),
    initialValue = initialValue,
    context = context,
    started = started,
)

/**
 * Combines the given *cold* [flows][flow1]  using the given [transform] function
 * and converts the result into a *hot* [StateFlow] that has its value stored in
 * the saved-state registry.
 *
 * @param flow1 A cold [Flow] to be combined.
 * @param flow2 A cold [Flow] to be combined.
 * @param flow3 A cold [Flow] to be combined.
 * @param initialValue The initial value of the [StateFlow].
 *   This value is also used when the state flow is reset using the
 *   [SharingStarted.WhileSubscribed] strategy with the `replayExpirationMillis`
 *   parameter.
 * @param started The strategy that controls when sharing is started and stopped.
 * @param context [CoroutineContext] that is added to the [viewModelScope] in
 *   which sharing is started. Defaults to [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows][flow1] before conversion to a [StateFlow].
 *
 * @return A [PropertyDelegateProvider] that provides the [StateFlow] as a
 *   [ReadOnlyProperty].
 */
public fun <T1, T2, T3, R> SavedStateViewModel.savedStateFlow(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    initialValue: R,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    transform: suspend (T1, T2, T3) -> R,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, StateFlow<R>>> = savedStateFlow(
    flow = combine(flow1, flow2, flow3, transform),
    initialValue = initialValue,
    context = context,
    started = started,
)

/**
 * Combines the given *cold* [flows][flow1]  using the given [transform] function
 * and converts the result into a *hot* [StateFlow] that has its value stored in
 * the saved-state registry.
 *
 * @param flow1 A cold [Flow] to be combined.
 * @param flow2 A cold [Flow] to be combined.
 * @param flow3 A cold [Flow] to be combined.
 * @param flow4 A cold [Flow] to be combined.
 * @param initialValue The initial value of the [StateFlow].
 *   This value is also used when the state flow is reset using the
 *   [SharingStarted.WhileSubscribed] strategy with the `replayExpirationMillis`
 *   parameter.
 * @param started The strategy that controls when sharing is started and stopped.
 * @param context [CoroutineContext] that is added to the [viewModelScope] in
 *   which sharing is started. Defaults to [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows][flow1] before conversion to a [StateFlow].
 *
 * @return A [PropertyDelegateProvider] that provides the [StateFlow] as a
 *   [ReadOnlyProperty].
 */
public fun <T1, T2, T3, T4, R> SavedStateViewModel.savedStateFlow(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    initialValue: R,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    transform: suspend (T1, T2, T3, T4) -> R,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, StateFlow<R>>> = savedStateFlow(
    flow = combine(flow1, flow2, flow3, flow4, transform),
    initialValue = initialValue,
    context = context,
    started = started,
)

/**
 * Combines the given *cold* [flows][flow1]  using the given [transform] function
 * and converts the result into a *hot* [StateFlow] that has its value stored in
 * the saved-state registry.
 *
 * @param flow1 A cold [Flow] to be combined.
 * @param flow2 A cold [Flow] to be combined.
 * @param flow3 A cold [Flow] to be combined.
 * @param flow4 A cold [Flow] to be combined.
 * @param flow5 A cold [Flow] to be combined.
 * @param initialValue The initial value of the [StateFlow].
 *   This value is also used when the state flow is reset using the
 *   [SharingStarted.WhileSubscribed] strategy with the `replayExpirationMillis`
 *   parameter.
 * @param started The strategy that controls when sharing is started and stopped.
 * @param context [CoroutineContext] that is added to the [viewModelScope] in
 *   which sharing is started. Defaults to [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows][flow1] before conversion to a [StateFlow].
 *
 * @return A [PropertyDelegateProvider] that provides the [StateFlow] as a
 *   [ReadOnlyProperty].
 */
public fun <T1, T2, T3, T4, T5, R> SavedStateViewModel.savedStateFlow(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    initialValue: R,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    transform: suspend (T1, T2, T3, T4, T5) -> R,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, StateFlow<R>>> = savedStateFlow(
    flow = combine(flow1, flow2, flow3, flow4, flow5, transform),
    initialValue = initialValue,
    context = context,
    started = started,
)
