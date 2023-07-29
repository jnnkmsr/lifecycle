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
import kotlinx.coroutines.flow.StateFlow
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

/**
 * Constructs a [MutableSavedStateFlow] that is stored in the saved-state registry.
 *
 * @param initialValue The initial value of the state flow.
 *
 * @return A [PropertyDelegateProvider] that provides the [MutableSavedStateFlow]
 *   as a [ReadOnlyProperty].
 */
public fun <T> SavedStateHandle.mutableSavedStateFlow(
    initialValue: T,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, MutableSavedStateFlow<T>>> =
    PropertyDelegateProvider { _, property ->
        val key = property.name
        val value = MutableSavedStateFlow(
            savedStateHandle = this,
            key = key,
            initialValue = initialValue,
        )
        ReadOnlyProperty { _, _ -> value }
    }

/**
 * A mutable [StateFlow] that is stored in the saved-state registry.
 */
public class MutableSavedStateFlow<T> private constructor(
    /**
     * The delegated [StateFlow] instance obtained from the [SavedStateHandle].
     */
    private val delegate: StateFlow<T>,
    /**
     * The identifier for storing the [StateFlow] into the saved-state registry.
     */
    private val key: String,
    /**
     * The [SavedStateHandle] instance.
     */
    private val savedStateHandle: SavedStateHandle,
) : StateFlow<T> by delegate {

    /**
     * Constructs a new [MutableSavedStateFlow] instance.
     *
     * @param initialValue The initial value of the [StateFlow].
     * @param key The identifier for storing the [StateFlow] into the
     *   saved-state registry.
     * @param savedStateHandle The [SavedStateHandle] instance.
     */
    internal constructor(
        initialValue: T,
        key: String,
        savedStateHandle: SavedStateHandle,
    ) : this(
        delegate = savedStateHandle.getStateFlow(key, initialValue),
        key = key,
        savedStateHandle = savedStateHandle,
    )

    /**
     * The current value of the state flow stored in the saved-state registry.
     */
    override var value: T
        get() = delegate.value
        set(value) {
            savedStateHandle[key] = value
        }
}
