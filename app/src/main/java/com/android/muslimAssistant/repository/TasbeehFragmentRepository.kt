package com.android.muslimAssistant.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TasbeehFragmentRepository(private val context: Context) {

    private val highestValueKey: Preferences.Key<Int> = intPreferencesKey("highestValue")
    private val counterKey: Preferences.Key<Int> = intPreferencesKey("counterKey")
    private val indicatorMaxKey: Preferences.Key<Int> = intPreferencesKey("indicatorMaxKey")

    suspend fun highestValueIncrement() {
        context.dataStore.edit {
            val value = it[highestValueKey] ?: 0
            it[highestValueKey] = value + 1
        }
    }

    suspend fun updateIndicatorMax(indicatorMax: Int) {
        context.dataStore.edit {
            it[indicatorMaxKey] = indicatorMax
        }
    }

    suspend fun incrementCounter() {
        context.dataStore.edit {
            val value = it[counterKey] ?: 0
            it[counterKey] = value + 1
        }
    }

    suspend fun resetCounter() {
        context.dataStore.edit {
            it[counterKey] = 0
        }
    }

    suspend fun resetHighestValue() {
        context.dataStore.edit {
            it[highestValueKey] = 0
        }
    }

    val indicatorMax: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[indicatorMaxKey] ?: 33
    }

    val highestValue: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[highestValueKey] ?: 0
    }

    val counterValue: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[counterKey] ?: 0
    }
}