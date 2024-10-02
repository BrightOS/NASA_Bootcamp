package ru.berserkers.deepspace.navigator.managers

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import ru.berserkers.deepspace.navigator.pointing.VectorPointing

class SensorManager(private val manager: SensorManager) : AbstractManager, SensorEventListener {

    private val rotationSensor: Sensor? = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    override var pointing: VectorPointing? = null

    override fun start() {
        manager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun stop() {
        manager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor != rotationSensor) return
        pointing?.setPhoneSensorValues(event.values)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }
}
