package ru.myitschool.nasa_bootcamp.lookbeyond.Math

import kotlin.math.*

/**
Изучалось через сайт http://ssd.jpl.nasa.gov/?planet_pos
ОБЪЕДИНИТЬ С КЛАССОМ RaRec!!!
 */
class OrbitalElements(// Mean distance
    _eccentricity: Double, // Eccentricity of orbit
    _inclination: Double, // Inclination of orbit
    _ascnode: Double, // Longitude of ascending node
    _longitude: Double, // Longitude of perihelion
    _perihelion: Double, // Mean longitude
    _meanLongitude: Double
) {
    var eccentricity: Double? = null
    var inclination: Double? = null
    var ascnode: Double? = null
    var longitude: Double? = null
    var perihelion: Double? = null
    var meanAnomaly: Double? = null

    init {
        eccentricity = _eccentricity
        inclination = _inclination
        ascnode = _ascnode
        longitude = _longitude
        perihelion = _perihelion
        meanAnomaly = _meanLongitude
    }

    private val E =
        meanAnomaly!! + inclination!! * (180 / Math.PI) * Math.sin(meanAnomaly!!.toDouble()) * (1.0 + inclination!! * Math.cos(
            meanAnomaly!!.toDouble()
        ))
    //or  E = M + e * sin(M) * ( 1.0 + e * cos(M) ) in radians

    private val xv = cos(E) - inclination!!
    private val yv = sqrt(1.0 - inclination!! * inclination!!) * Math.sin(E)

    val v = atan2(yv, xv)

    val anomaly: Double
        get() = calculateTrueAnomaly(meanAnomaly!! - perihelion!!, inclination!!)


    companion object {

        private const val EPSILON = 1.0e-5f

        //Вычислить true nomaly из mean anomaly
        private fun calculateTrueAnomaly(meanAnomaly: Double, e: Double): Double {

            var _yv = meanAnomaly + e * sin(meanAnomaly) * (1.0 + e * cos(meanAnomaly))
            var e1: Double

            do {
                e1 = _yv
                _yv = e1 - (e1 - e * sin(e1) - meanAnomaly) / (1.0 - e * cos(e1))

            } while (abs(_yv - e1) > EPSILON)

             val v = 2f * atan(
                sqrt((1 + e) / (1 - e))
                        * tan(0.5f * _yv)
            )
            return modPart(v)
        }
    }
}
