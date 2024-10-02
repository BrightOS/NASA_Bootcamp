package ru.berserkers.deepspace.navigator.rendertype

import android.graphics.Color
import ru.berserkers.deepspace.maths.RaDec
import ru.berserkers.deepspace.maths.coords.GeocentricCoordinates
import ru.berserkers.deepspace.utils.DEGREE_180
import ru.berserkers.deepspace.utils.DEGREE_360
import ru.berserkers.deepspace.utils.DEGREE_90
import java.util.*

internal class Grid(
    private val numRaSources: Int,
    private val numDecSources: Int, ) {

    private enum class Type {
        RA, DEC
    }

    private fun raDecInit(myRaDec: Double, koef: Int, func: (Int) -> Double, type: Type): RaDec {
        val raOrDec = func(koef)
        return if (type == Type.RA) RaDec(raOrDec, myRaDec) else RaDec(myRaDec, raOrDec)
    }

    private fun createRaLine(index: Int, numRaSources: Int): LineRun {
        val line = LineRun(Color.WHITE)
        val ra = index * DEGREE_360 / numRaSources
        with(line) {
            for (i in 0 until DEC_COUNT - 1) {
                val raDec = raDecInit(ra, i, ::getDec, Type.DEC)
                raDecs.add(raDec)
                vertices.add(GeocentricCoordinates.getInstance(raDec))
            }
            val raDec = RaDec(0.0, -DEGREE_90)
            raDecs.add(raDec)
            vertices.add(GeocentricCoordinates.getInstance(raDec))
        }
        return line
    }

    private fun createDecLine(dec: Double): LineRun {
        val line = LineRun(Color.WHITE)
        with(line) {
            for (i in 0 until RA_COUNT) {
                val raDec = raDecInit(dec, i, ::getRa, Type.RA)
                raDecs.add(raDec)
                vertices.add(GeocentricCoordinates.getInstance(raDec))
            }
            val raDec = RaDec(0.0, dec)
            raDecs.add(raDec)
            vertices.add(GeocentricCoordinates.getInstance(raDec))
        }
        return line
    }

    private fun getRa(koef: Int): Double {
        return koef * DEGREE_360 / RA_COUNT
    }

    private fun getDec(koef: Int) = DEGREE_90 - koef * DEGREE_180 / (DEC_COUNT - 1)

    fun initLine(): List<LineRun> {
        val lineSources = ArrayList<LineRun>()

        fun addMirrorDecs(dec: Double) {
            lineSources.add(createDecLine(dec))
            lineSources.add(createDecLine(-dec))
        }

        with(lineSources) {
            for (r in 0 until numRaSources) add(createRaLine(r, numRaSources))

            add(createDecLine(0.0)) // Экватор

            for (d in 1 until numDecSources) {
                val dec = d * DEGREE_90 / numDecSources
                addMirrorDecs(dec)
            }
        }
        return lineSources
    }

    companion object {
        private const val DEC_COUNT = 3
        private const val RA_COUNT = 36
    }
}
