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
import com.github.jnnkmsr.kotlin.flow.combineStatesIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Returns a [StateFlow] whose values are generated with [transform] function
 * by combining the most recently emitted values by each of the given
 * [StateFlow]s. The returned flow is started in `this` [SavedStateViewModel]'s
 * default [coroutineScope][SavedStateViewModel.coroutineScope], adding the
 * given [context].
 *
 * @param flow1 A [StateFlow] to be combined.
 * @param flow2 A [StateFlow] to be combined.
 * @param started The strategy that controls when sharing is started and stopped.
 *   Defaults to [SharingStarted.WhileSubscribed].
 * @param context Optional [CoroutineContext] that is added to `this`
 *   view model's default [coroutineScope][SavedStateViewModel.coroutineScope],
 *   to provide the scope in which sharing is started. Defaults to
 *   [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows][flow1].
 */
public fun <T1, T2, R> SavedStateViewModel.combineStates(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    transform: (T1, T2) -> R,
): StateFlow<R> = combineStatesIn(
    flow1 = flow1,
    flow2 = flow2,
    scope = coroutineScope + context,
    started = started,
    transform = transform,
)

/**
 * Returns a [StateFlow] whose values are generated with [transform] function
 * by combining the most recently emitted values by each of the given
 * [StateFlow]s. The returned flow is started in `this` [SavedStateViewModel]'s
 * default [coroutineScope][SavedStateViewModel.coroutineScope], adding the
 * given [context].
 *
 * @param flow1 A [StateFlow] to be combined.
 * @param flow2 A [StateFlow] to be combined.
 * @param flow3 A [StateFlow] to be combined.
 * @param started The strategy that controls when sharing is started and stopped.
 *   Defaults to [SharingStarted.WhileSubscribed].
 * @param context Optional [CoroutineContext] that is added to `this`
 *   view model's default [coroutineScope][SavedStateViewModel.coroutineScope],
 *   to provide the scope in which sharing is started. Defaults to
 *   [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows][flow1].
 */
public fun <T1, T2, T3, R> SavedStateViewModel.combineStates(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    transform: (T1, T2, T3) -> R,
): StateFlow<R> = combineStatesIn(
    flow1 = flow1,
    flow2 = flow2,
    flow3 = flow3,
    scope = coroutineScope + context,
    started = started,
    transform = transform,
)

/**
 * Returns a [StateFlow] whose values are generated with [transform] function
 * by combining the most recently emitted values by each of the given
 * [StateFlow]s. The returned flow is started in `this` [SavedStateViewModel]'s
 * default [coroutineScope][SavedStateViewModel.coroutineScope], adding the
 * given [context].
 *
 * @param flow1 A [StateFlow] to be combined.
 * @param flow2 A [StateFlow] to be combined.
 * @param flow3 A [StateFlow] to be combined.
 * @param flow4 A [StateFlow] to be combined.
 * @param started The strategy that controls when sharing is started and stopped.
 *   Defaults to [SharingStarted.WhileSubscribed].
 * @param context Optional [CoroutineContext] that is added to `this`
 *   view model's default [coroutineScope][SavedStateViewModel.coroutineScope],
 *   to provide the scope in which sharing is started. Defaults to
 *   [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows][flow1].
 */
public fun <T1, T2, T3, T4, R> SavedStateViewModel.combineStates(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    transform: (T1, T2, T3, T4) -> R,
): StateFlow<R> = combineStatesIn(
    flow1 = flow1,
    flow2 = flow2,
    flow3 = flow3,
    flow4 = flow4,
    scope = coroutineScope + context,
    started = started,
    transform = transform,
)

/**
 * Returns a [StateFlow] whose values are generated with [transform] function
 * by combining the most recently emitted values by each of the given
 * [StateFlow]s. The returned flow is started in `this` [SavedStateViewModel]'s
 * default [coroutineScope][SavedStateViewModel.coroutineScope], adding the
 * given [context].
 *
 * @param flow1 A [StateFlow] to be combined.
 * @param flow2 A [StateFlow] to be combined.
 * @param flow3 A [StateFlow] to be combined.
 * @param flow4 A [StateFlow] to be combined.
 * @param flow5 A [StateFlow] to be combined.
 * @param started The strategy that controls when sharing is started and stopped.
 *   Defaults to [SharingStarted.WhileSubscribed].
 * @param context Optional [CoroutineContext] that is added to `this`
 *   view model's default [coroutineScope][SavedStateViewModel.coroutineScope],
 *   to provide the scope in which sharing is started. Defaults to
 *   [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows][flow1].
 */
public fun <T1, T2, T3, T4, T5, R> SavedStateViewModel.combineStates(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    flow5: StateFlow<T5>,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    transform: (T1, T2, T3, T4, T5) -> R,
): StateFlow<R> = combineStatesIn(
    flow1 = flow1,
    flow2 = flow2,
    flow3 = flow3,
    flow4 = flow4,
    flow5 = flow5,
    scope = coroutineScope + context,
    started = started,
    transform = transform,
)

/**
 * Returns a [StateFlow] whose values are generated with [transform] function
 * by combining the most recently emitted values by each of the given
 * [StateFlow]s. The returned flow is started in `this` [SavedStateViewModel]'s
 * default [coroutineScope][SavedStateViewModel.coroutineScope], adding the
 * given [context].
 *
 * @param flows The [StateFlow]s to be combined.
 * @param started The strategy that controls when sharing is started and stopped.
 *   Defaults to [SharingStarted.WhileSubscribed].
 * @param context Optional [CoroutineContext] that is added to `this`
 *   view model's default [coroutineScope][SavedStateViewModel.coroutineScope],
 *   to provide the scope in which sharing is started. Defaults to
 *   [EmptyCoroutineContext].
 * @param transform The transform to apply to combine the most recent values of
 *   the original [flows].
 */
public inline fun <reified T, R> SavedStateViewModel.combineStates(
    vararg flows: StateFlow<T>,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline transform: (Array<T>) -> R,
): StateFlow<R> = combineStatesIn(
    *flows,
    scope = coroutineScope + context,
    started = started,
    transform = transform,
)
