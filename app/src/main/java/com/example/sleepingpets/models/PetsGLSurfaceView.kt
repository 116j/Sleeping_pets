package com.example.sleepingpets.models

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.HardwareRenderer
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.sleepingpets.models.db_models.Pet


class PetsGLSurfaceView(val con: Context, val renderer: PetsGLRenderer) : GLSurfaceView(con) {

    private val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f
    private var previousX: Float = 0f

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        setRenderer(renderer)
        // Set the Renderer for drawing on the GLSurfaceView
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        holder.setFormat(PixelFormat.TRANSLUCENT);
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        val x: Float = e.x

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {

                var dx: Float = x - previousX

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }

                // reverse direction of rotation to left of the mid-line


                renderer.angle += (dx) * TOUCH_SCALE_FACTOR
                requestRender()
            }
        }

        previousX = x
        return true
    }

}