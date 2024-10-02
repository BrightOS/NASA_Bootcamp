package ru.berserkers.deepspace.navigator.pointing

import com.example.math_module.geometry.Vector3D
import ru.berserkers.deepspace.maths.coords.GeocentricCoordinates
import ru.berserkers.deepspace.maths.coords.LatLong
import java.util.*


interface AbstractPointing {
    var viewDegree: Double
    val time: Date
    var location: LatLong
    val up: Vector3D
    val timeMillis: Long

    class Pointing @JvmOverloads constructor(
        val userLook: GeocentricCoordinates = GeocentricCoordinates(1.0, 0.0, 0.0),
        val perpendicular: GeocentricCoordinates = GeocentricCoordinates(0.0, 1.0, 0.0),
    ) {

        fun updatePerpendicular(newPerpendicular: Vector3D) {
            perpendicular.update(newPerpendicular)
        }

        fun updateUserLook(_userLook: Vector3D) {
            userLook.update(_userLook)
        }
    }

    fun setHorizontalRotation(value: Boolean)
    fun setPointing(lineOfSight: Vector3D, perpendicular: Vector3D)
    fun setPhoneSensorValues(acceleration: Vector3D, magneticField: Vector3D)
    fun setPhoneSensorValues(rotationVector: FloatArray)
    fun setMagneticDeclination(calculator: MagneticDeclination)
}