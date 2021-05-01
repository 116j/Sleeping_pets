package com.example.sleepingpets.models

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.*


class Torus(private val fileName: String, context: Context?) {
    private val verticesList: List<String>
    private val facesList: List<String>
    private var verticesBuffer: FloatBuffer = FloatBuffer.allocate(0)
    private var facesBuffer: ShortBuffer = ShortBuffer.allocate(0)
    private var program = 0
    private val vertexShaderCode =
        "attribute vec4 position;\n" +
                "uniform mat4 matrix;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_Position = matrix * position;\n" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_FragColor = vec4(1, 0.5, 0, 1.0);\n" +
                "}"

    init {
        verticesList = ArrayList()
        facesList = ArrayList()

        // More code goes here
        val scanner = Scanner(context!!.assets.open(fileName))

// Loop through all its lines

// Loop through all its lines
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            if (line.startsWith("v ")) {
                // Add vertex line to list of vertices
                verticesList.add(line)
            } else if (line.startsWith("f ")) {
                // Add face line to faces list
                facesList.add(line)
            }
        }

// Close the scanner

// Close the scanner
        scanner.close()
        val buffer1: ByteBuffer = ByteBuffer.allocateDirect(verticesList.size * 3 * 4)
        buffer1.order(ByteOrder.nativeOrder())
        verticesBuffer = buffer1.asFloatBuffer()
        val buffer2 =
            ByteBuffer.allocateDirect(facesList.size * 3 * 2)
        buffer2.order(ByteOrder.nativeOrder())
        facesBuffer = buffer2.asShortBuffer()
        for (vertex in verticesList) {
            val coords =
                vertex.split(" ").toTypedArray() // Split by space
            val x = coords[1].toFloat()
            val y = coords[2].toFloat()
            val z = coords[3].toFloat()
            verticesBuffer.put(x)
            verticesBuffer.put(y)
            verticesBuffer.put(z)
        }
        verticesBuffer.position(0)
        for (face in facesList) {
            val vertexIndices = face.split(" ").toTypedArray()
            val vertex1 = vertexIndices[1].toShort()
            val vertex2 = vertexIndices[2].toShort()
            val vertex3 = vertexIndices[3].toShort()
            facesBuffer.put((vertex1 - 1).toShort())
            facesBuffer.put((vertex2 - 1).toShort())
            facesBuffer.put((vertex3 - 1).toShort())
        }
        facesBuffer.position(0)


        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, vertexShaderCode)

        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode)
        GLES20.glCompileShader(vertexShader)
        GLES20.glCompileShader(fragmentShader)

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);
    }

    fun draw() {
        val position = GLES20.glGetAttribLocation(program, "position")
        GLES20.glEnableVertexAttribArray(position)
        GLES20.glVertexAttribPointer(
            position,
            3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer
        );
        val projectionMatrix = FloatArray(16)
        val viewMatrix = FloatArray(16)

        val productMatrix = FloatArray(16)
        Matrix.frustumM(
            projectionMatrix, 0,
            -1.0f, 1.0f,
            -1.0f, 1.0f,
            2.0f, 9.0f
        );
        Matrix.setLookAtM(
            viewMatrix, 0,
            0.0f, 3.0f, -4.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f
        );
        Matrix.multiplyMM(
            productMatrix, 0,
            projectionMatrix, 0,
            viewMatrix, 0
        );
        val matrix = GLES20.glGetUniformLocation(program, "matrix")
        GLES20.glUniformMatrix4fv(matrix, 1, false, productMatrix, 0)
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            facesList.size * 3, GLES20.GL_UNSIGNED_SHORT, facesBuffer
        );
        GLES20.glDisableVertexAttribArray(position);
    }
}
