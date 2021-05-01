package com.example.sleepingpets.models

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.opengles.GL10


class PetsGLRenderer(private val obj:String, val context: Context) : GLSurfaceView.Renderer {
    private lateinit var torus : Torus
    @Volatile
    var angle: Float = 0f
    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        torus.draw();
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        gl?.glDisable(GL10.GL_DITHER);
        gl?.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
            GL10.GL_FASTEST);

        gl?.glClearColor(0.0f,0.0f,0.0f,0.0f);
        gl?.glEnable(GL10.GL_CULL_FACE);
        gl?.glShadeModel(GL10.GL_SMOOTH);
        gl?.glEnable(GL10.GL_DEPTH_TEST);

        torus = Torus(obj,context)
    }
}