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

@file:SuppressLint("RestrictedApi")

package com.github.jnnkmsr.lifecycle.viewmodel

import android.annotation.SuppressLint
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jnnkmsr.lifecycle.flow.MutableSavedStateFlow
import com.github.jnnkmsr.lifecycle.flow.mutableSavedStateFlow
import com.github.jnnkmsr.lifecycle.flow.savedStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

/**
 * A viewmodel that has access to the saved-state registry via a
 * [SavedStateHandle].
 */
public interface SavedStateViewModel {

    /**
     * The default [CoroutineScope] used by this viewmodel. Typically, this will
     * be [viewModelScope] of implementing [ViewModel].
     */
    @get:RestrictTo(Scope.SUBCLASSES)
    public val coroutineScope: CoroutineScope


    /**
     * The [SavedStateHandle] instance, typically, obtained by constructor
     * injection.
     */
    @get:RestrictTo(Scope.SUBCLASSES)
    public val savedStateHandle: SavedStateHandle

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
            scope = coroutineScope + context,
            started = started,
        )

    /**
     * Converts `this` _cold_ [Flow] into a _hot_ [SharedFlow] that is started
     * in the default [coroutineScope]. Delegates to [shareIn] using
     * [coroutineScope] together with the given [context].
     *
     * @param started The strategy that controls when sharing is started and stopped.
     * @param context Optional [CoroutineContext] that is added to `this`
     *   view model's default [coroutineScope], to provide the scope in which
     *   sharing is started. Defaults to [EmptyCoroutineContext].
     * @param replay The number values replayed to new subscribers (cannot be
     *   negative; defaults to zero).
     *
     * @see shareIn
     */
    public fun <T> Flow<T>.share(
        started: SharingStarted = SharingStarted.WhileSubscribed(),
        context: CoroutineContext = EmptyCoroutineContext,
        replay: Int = 0,
    ): SharedFlow<T> = shareIn(
        scope = coroutineScope + context,
        started = started,
        replay = replay,
    )

    /**
     * Converts `this` _cold_ [Flow] into a _hot_ [StateFlow] that is started
     * in the default [coroutineScope]. Delegates to [shareIn] using
     * [coroutineScope] together with the given [context].
     *
     * @param initialValue The initial value of the [StateFlow]. This value is
     *   also used when the state flow is reset using the
     *   [SharingStarted.WhileSubscribed] strategy with the
     *   `replayExpirationMillis` parameter.
     * @param started The strategy that controls when sharing is started and stopped.
     * @param context Optional [CoroutineContext] that is added to `this`
     *   view model's default [coroutineScope], to provide the scope in which
     *   sharing is started. Defaults to [EmptyCoroutineContext].
     *
     * @see stateIn
     */
    public fun <T> Flow<T>.state(
        initialValue: T,
        started: SharingStarted = SharingStarted.WhileSubscribed(),
        context: CoroutineContext = EmptyCoroutineContext,
    ): StateFlow<T> = stateIn(
        scope = coroutineScope + context,
        started = started,
        initialValue = initialValue,
    )
}
