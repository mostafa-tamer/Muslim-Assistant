package com.android.muslimAssistant.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

 val Context.dataStore by preferencesDataStore(name = "SharedPreferencesDataStore")

class SharedPreferencesRepository(private val context: Context) {

    private val latitudeKey = doublePreferencesKey("latitude")
    private val longitudeKey = doublePreferencesKey("longitude")
    private val methodKey = stringPreferencesKey("method")
    private val languageKey = stringPreferencesKey("language")
    private val notificationKey = booleanPreferencesKey("isNotification")

    suspend fun updateLatLng(latitude: Double, longitude: Double) {
        context.dataStore.edit {
            it[latitudeKey] = latitude
            it[longitudeKey] = longitude
        }
    }

    suspend fun updateMethod(method: String) {
        context.dataStore.edit {
            it[methodKey] = method
        }
    }

    suspend fun isNotification(isNotification: Boolean) {
        context.dataStore.edit {
            it[notificationKey] = isNotification
        }
    }

    suspend fun updateLanguageSelection(lang: String) {
        context.dataStore.edit {
            it[languageKey] = lang
        }
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[languageKey] ?: "en"
    }

    val latitude: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[latitudeKey] ?: 0.0
    }

    val longitude: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[longitudeKey] ?: 0.0
    }

    val method: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[methodKey] ?: "-1"
    }

    val isNotification: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[notificationKey] ?: true
    }
}