package com.yuchen.makeplan.view

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View


class GanttTaskBar : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val barPaint = Paint()

    private var startPosRate : Float = 0f
    private var endPosRate : Float = 0f
    private var completePosRate : Float = 0f
    private var colorString = ""
    private var left = 0f
    private var right = 0f
    private var top = 0f
    private var bottom = 0f

    fun taskBarInit(projectStart : Long, projectEnd : Long, taskStart : Long, taskEnd : Long,percent : Int,color : String){
        startPosRate = (taskStart-projectStart).toFloat()/(projectEnd - projectStart).toFloat()
        endPosRate = (taskEnd - projectStart).toFloat()/(projectEnd - projectStart).toFloat()
        completePosRate = startPosRate + (endPosRate - startPosRate)*percent.toFloat()/100f
        colorString = color
    }

    private fun drawTaskBar(canvas: Canvas) {
        barPaint.color = Color.parseColor("#$colorString")
//        Log.d("chenyjzn","width = $width, LayoutWidth = ${minimumWidth}")
        left = startPosRate * width.toFloat()
        top = 0.0f
        right = endPosRate * width.toFloat()
        bottom = height.toFloat()

        canvas.drawRoundRect(left,top,right,bottom,15f,15f,barPaint)
        barPaint.color = Color.WHITE
        canvas.drawRect(left,0.0f,completePosRate*width.toFloat(),height.toFloat(),barPaint)
    }

    fun isClick(x:Float,y:Float):Boolean{
        return x in left..right && y in top..bottom
    }

    override fun onDraw(canvas: Canvas) {
        drawTaskBar(canvas)
    }
}