package com.yuchen.makeplan.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.ext.toDp
import com.yuchen.makeplan.ext.toPx
import java.util.*

class GanttTaskTimeLine : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var calendar: Calendar = Calendar.getInstance()
    private var startDate: Long = 0L
    private var endDate: Long = 0L

    private var startYear: Int = calendar.get(Calendar.YEAR)
    private var startMonth: Int = calendar.get(Calendar.MONTH)
    private var startDay: Int = calendar.get(Calendar.DAY_OF_MONTH)

    private var endYear: Int = calendar.get(Calendar.YEAR)
    private var endMonth: Int = calendar.get(Calendar.MONTH)
    private var endDay: Int = calendar.get(Calendar.DAY_OF_MONTH)

    private var timeLineType: Int = 0

    private val linePaint = Paint().apply {
        strokeWidth = 1.toPx().toFloat()
        color = Color.GRAY
    }
    private val textPaint = Paint().apply {
        color = Color.GRAY
        textSize = 16.toPx().toFloat()
        textAlign = Paint.Align.CENTER
    }

    val fontOffsetY = -(textPaint.fontMetrics.top + textPaint.fontMetrics.bottom)/2

    fun setRange(_startDate: Long, _endDate: Long){
        this.startDate = _startDate
        calendar.timeInMillis = _startDate
        startYear = calendar.get(Calendar.YEAR)
        startMonth = calendar.get(Calendar.MONTH)
        startDay = calendar.get(Calendar.DAY_OF_MONTH)
        this.endDate = _endDate
        calendar.timeInMillis = _endDate
        endYear = calendar.get(Calendar.YEAR)
        endMonth = calendar.get(Calendar.MONTH)
        endDay= calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun setProjectTimeByDx(dx : Float, width : Int){
        var timeOffset = ((endDate - startDate).toFloat()*dx/width.toFloat()).toLong()
        setRange(startDate - timeOffset, endDate - timeOffset)
    }

    fun setProjectTimeByDlDr(dl : Float, dr : Float, width : Int){
        var timeOffsetl = ((endDate - startDate).toFloat() * dl / width.toFloat()).toLong()
        var timeOffsetr = ((endDate - startDate).toFloat() * dr / width.toFloat()).toLong()
        setRange(startDate + timeOffsetl, endDate - timeOffsetr)
    }

    private fun drawFrame(canvas: Canvas){
        //Upper line
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, linePaint)
        //Button line
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), linePaint)
    }

    private fun drawSecondaryTimeLineByDay(canvas: Canvas){
        calendar.set(endYear,endMonth,endDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,-1)
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){

            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                0f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                height.toFloat(),
                linePaint
            )

            actualTime += DAY_MILLIS
        }
    }

    private fun drawSecondaryTimeLineByWeek(canvas: Canvas){
        calendar.set(endYear,endMonth,endDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,8-calendar.get(Calendar.DAY_OF_WEEK))
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,1-calendar.get(Calendar.DAY_OF_WEEK))
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){

            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                0f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                height.toFloat(),
                linePaint
            )

            actualTime += 7* DAY_MILLIS
        }
    }

    private fun drawSecondaryTimeLineByMonth(canvas: Canvas){
        calendar.set(endYear,endMonth,1,0,0,0)
        calendar.add(Calendar.MONTH,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,1,0,0,0)
        calendar.add(Calendar.MONTH,-1)
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){
            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                0f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                height.toFloat(),
                linePaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            actualTime += days * DAY_MILLIS
        }
    }

    fun calScale(Scale : Long) : Float{
        return ((Scale.toFloat()/(endDate - startDate).toFloat())*width.toFloat()).toDp()
    }

    fun setTimeLineScale(){
        if (calScale(DAY_MILLIS)>=25.0f){
            timeLineType = 0
        } else if(calScale(7 * DAY_MILLIS)>=25.0f){
            timeLineType = 1
        }else if(calScale(28 * DAY_MILLIS)>=25.0f){
            timeLineType = 3
        }else{
            timeLineType = 4
        }
        //Log.d("chenyjzn", " Scale = ${calScale(DAY_MILLIS)}, type = $timeLineType")
    }

    fun interpolation(startTime : Long, endTime : Long, actualTime : Long) : Float{
        return (actualTime-startTime).toFloat()/(endTime - startTime).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        drawFrame(canvas)
        setTimeLineScale()
        when(timeLineType){
            0 -> {
                drawSecondaryTimeLineByDay(canvas)
            }
            1 -> {
                drawSecondaryTimeLineByWeek(canvas)
            }
            2 -> {
                drawSecondaryTimeLineByMonth(canvas)
            }
            else -> {
                drawSecondaryTimeLineByMonth(canvas)
            }
        }
    }
}