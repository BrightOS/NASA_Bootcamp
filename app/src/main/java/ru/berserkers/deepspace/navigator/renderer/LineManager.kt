package ru.berserkers.deepspace.navigator.renderer

import android.graphics.Color
import com.example.math_module.geometry.Vector3D
import com.example.math_module.geometry.difference
import com.example.math_module.geometry.mult
import com.example.math_module.geometry.normalized
import com.example.math_module.geometry.sum
import ru.berserkers.deepspace.R
import ru.berserkers.deepspace.navigator.rendertype.LineRun
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.math.tan

class LineManager(layer: Int, textureManager: TextureManager) :
    Renderer(layer, textureManager) {

    private lateinit var texture: AbstractTexture

    private val buffers = mapOf(
        VERTEX_KEY to VertexBuffer(),
        COLOR_KEY to ColorBuffer(),
        COORD_KEY to TexCoordBuffer(),
        INDEX_KEY to IndexBuffer()
    )

    fun updateObjects(lines: List<LineRun>) {

        initBuffers(lines)

        val fovyInRadians = 60 * PI / 180.0
        val sizeFactor = tan(fovyInRadians * 0.5) / LINE_WIDTH

        var vertexIndex: Short = 0
        for (line in lines) {
            val coords = line.vertices
            if (coords.size < 2) continue

            for (i in 0 until coords.size - 1) {
                val p1: Vector3D = coords[i]
                val p2: Vector3D = coords[i + 1]
                val u = difference(p2, p1)

                val sum = sum(p1, p2)
                sum.scale(1.5)
                val v = u.mult(sum).normalized()
                v.scale(sizeFactor * line.lineWidth)

                setupVertex(p1, v, p2)
                setupColor(Color.WHITE)
                setupCoord()

                val bottomLeft = vertexIndex++
                val topLeft = vertexIndex++
                val bottomRight = vertexIndex++
                val topRight = vertexIndex++

                setupTriangle(bottomLeft, topLeft, bottomRight, topRight)
            }
        }
    }

    private fun initBuffers(lines: List<LineRun>) {
        var numLineSegments = 0
        for (l in lines) {
            numLineSegments += l.vertices.size - 1
        }

        val numVertices = 4 * numLineSegments
        val numIndices = 6 * numLineSegments

        buffers.apply {
            getValue(VERTEX_KEY).vertexCount = 4 * numLineSegments
            getValue(COLOR_KEY).vertexCount = 4 * numLineSegments
            getValue(COORD_KEY).vertexCount = numVertices
            getValue(INDEX_KEY).vertexCount = numIndices
        }
    }

    private fun setupTriangle(bottomLeft: Short, topLeft: Short, bottomRight: Short, topRight: Short) {
        (buffers.getValue(INDEX_KEY) as IndexBuffer).also {
            it.addIndex(bottomLeft)
            it.addIndex(topLeft)
            it.addIndex(bottomRight)

            it.addIndex(bottomRight)
            it.addIndex(topLeft)
            it.addIndex(topRight)
        }
    }

    private fun setupCoord() {
        (buffers.getValue(COORD_KEY) as TexCoordBuffer).also {
            it.addTexCoords(0f, 0f)
            it.addTexCoords(0f, 1f)
            it.addTexCoords(1f, 1f)
            it.addTexCoords(1f, 0f)
        }
    }

    private fun setupColor(color: Int) {
        (buffers.getValue(COLOR_KEY) as ColorBuffer).also {
            it.colorBuffer.put(color)
            it.colorBuffer.put(color)
            it.colorBuffer.put(color)
            it.colorBuffer.put(color)
        }
    }

    private fun setupVertex(
        p1: Vector3D,
        v: Vector3D,
        p2: Vector3D,
    ) {
        (buffers.getValue(VERTEX_KEY) as VertexBuffer).also {
            it.addPoint(difference(p1, v))
            it.addPoint(sum(p1, v))
            it.addPoint(difference(p2, v))
            it.addPoint(sum(p2, v))
        }
    }

    override fun reload(gl: GL10, fullReload: Boolean) {
        texture = textureManager().getTextureFromResource(gl, R.drawable.line)
    }

    override fun drawInternal(gl: GL10) {
        if ((buffers.getValue(INDEX_KEY) as IndexBuffer).vertexCount == 0) return
        gl.apply {
            setupClientState()
            texture.bind(this)

            glFrontFace(GL10.GL_CW)

            for (key in buffers.keys) {
                buffers.getValue(key).set(this)
            }

            glDisable(GL10.GL_TEXTURE_2D)
            glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        }
    }

    private fun GL10.setupClientState() {
        glEnableClientState(GL10.GL_VERTEX_ARRAY)
        glEnableClientState(GL10.GL_COLOR_ARRAY)
        glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
    }

    companion object {
        const val LINE_WIDTH = 1480
        const val VERTEX_KEY = "vertex"
        const val COLOR_KEY = "color"
        const val COORD_KEY = "coord"
        const val INDEX_KEY = "index"
    }
}