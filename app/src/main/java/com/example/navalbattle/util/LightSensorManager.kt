package com.example.navalbattle.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Manages the light sensor to detect ambient light levels and suggest theme changes.
 * Emits theme updates (light/dark) via a StateFlow based on light sensor data.
 */
class LightSensorManager(context: Context) {
    // Sensor manager for accessing device sensors
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    // Light sensor for detecting ambient light levels
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    // StateFlow to emit whether the light theme should be used
    private val _isLightTheme = MutableStateFlow(true)
    val isLightTheme: StateFlow<Boolean> = _isLightTheme.asStateFlow()

    // Tracks whether the sensor is active (can be disabled for manual theme control)
    var isSensorActive = false

    // Listener for light sensor events
    private val lightSensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_LIGHT && isSensorActive) {
                val lux = event.values[0]
                // Update theme based on ambient light (light theme if lux > 20,000)
                _isLightTheme.value = lux > LIGHT_THEME_LUX_THRESHOLD
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // No action needed for accuracy changes
        }
    }

    companion object {
        // Threshold for switching to light theme (in lux)
        private const val LIGHT_THEME_LUX_THRESHOLD = 20000f
    }

    /**
     * Starts listening to light sensor events after a specified delay.
     * @param delayMs Delay in milliseconds before activating the sensor
     */
    fun startListening(delayMs: Long = 100L) {
        if (lightSensor != null) {
            kotlinx.coroutines.GlobalScope.launch {
                kotlinx.coroutines.delay(delayMs)
                isSensorActive = true
                sensorManager.registerListener(
                    lightSensorListener,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
    }

    /**
     * Stops listening to light sensor events and deactivates the sensor.
     */
    fun stopListening() {
        isSensorActive = false
        sensorManager.unregisterListener(lightSensorListener)
    }

    /**
     * Disables sensor-based theme updates, allowing manual theme control.
     */
    fun disableSensor() {
        isSensorActive = false
    }

    /**
     * Sets the theme manually and disables sensor updates.
     * @param isLight Whether to use the light theme
     */
    fun setThemeManually(isLight: Boolean) {
        disableSensor()
        _isLightTheme.value = isLight
    }
}