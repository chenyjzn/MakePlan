package com.yuchen.makeplan.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.yuchen.makeplan.ext.toPx

class GanttTaskBar : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var projectStartDate: Long = 0L
    private var projectEndDate: Long = 0L

    private var taskStartDate: Long = 0L
    private var taskEndDate: Long = 0L

    var taskName = ""
    var colorString = "000000"

    private val linePaint = Paint().apply {
        strokeWidth = 1.toPx().toFloat()
        color = Color.GRAY
    }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 16.toPx().toFloat()
        textAlign = Paint.Align.LEFT
    }

    private val barPaint = Paint().apply {
        color = Color.parseColor("#$colorString")
    }

    private val textBackGroundPaint = Paint().apply {
        color = Color.WHITE
    }

    val fontOffsetY = -(textPaint.fontMetrics.top + textPaint.fontMetrics.bottom)/2

    fun setRange(_projectStartDate: Long, _projectEndDate: Long, _taskStartDate: Long, _taskEndDate: Long){
        this.projectStartDate = _projectStartDate
        this.projectEndDate = _projectEndDate
        this.taskStartDate = _taskStartDate
        this.taskEndDate = _taskEndDate
    }

    private fun drawTaskBar(canvas: Canvas) {
        val left = interpolation(projectStartDate,projectEndDate,taskStartDate)*width.toFloat()
        val right = interpolation(projectStartDate,projectEndDate,taskEndDate)*width.toFloat()
        val bottom = height.toFloat()*0.5f
        val top = height.toFloat()
        val bounds = Rect()
        textPaint.getTextBounds(taskName, 0, taskName.length, bounds)
        //canvas.drawRect(left,0f,left + bounds.right- bounds.left, height.toFloat()*0.5f, textBackGroundPaint)
        canvas.drawText(taskName,left,height.toFloat()*0.25f + fontOffsetY, textPaint)
        canvas.drawRoundRect(left,top,right , bottom,15f,15f, barPaint)
    }

    fun interpolation(startTime : Long, endTime : Long, actualTime : Long) : Float{
        return (actualTime-startTime).toFloat()/(endTime - startTime).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        barPaint.color = Color.parseColor("#$colorString")
        drawTaskBar(canvas)
    }
}