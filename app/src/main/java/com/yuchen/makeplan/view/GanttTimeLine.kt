package com.yuchen.makeplan.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.util.TimeUtil.millisToDay
import com.yuchen.makeplan.util.TimeUtil.millisToMonth
import com.yuchen.makeplan.util.TimeUtil.millisToYear
import com.yuchen.makeplan.util.TimeUtil.millisToYearMonth
import com.yuchen.makeplan.ext.toDp
import com.yuchen.makeplan.ext.toPx
import java.util.*

class GanttTimeLine : View{

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var calendar: Calendar = Calendar.getInstance()
    private var startDate: Long = 0L
    private var endDate: Long = 0L
    private var duration: Long = 0L

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
        color = Color.WHITE
        textSize = 16.toPx().toFloat()
        textAlign = Paint.Align.CENTER
    }

    private fun getDaysAgo(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysAgo)

        return calendar.time
    }

    val fontOffsetY = -(textPaint.fontMetrics.top + textPaint.fontMetrics.bottom)/2

    fun setProjectTimeByDx(dx : Float, width : Int){
        var timeOffset = ((endDate - startDate).toFloat()*dx/width.toFloat()).toLong()
        setRange(startDate - timeOffset, endDate - timeOffset)
    }

    fun setProjectTimeByDlDr(dl : Float, dr : Float, width : Int){
        var timeOffsetl = ((endDate - startDate).toFloat() * dl / width.toFloat()).toLong()
        var timeOffsetr = ((endDate - startDate).toFloat() * dr / width.toFloat()).toLong()
        setRange(startDate + timeOffsetl, endDate - timeOffsetr)
    }

    fun setRange(_startDate: Long, _endDate: Long){
        //Log.d("chenyjzn","start $_startDate, end $_endDate")
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
//        Log.d("chenyjzn", " Scale = ${calScale(DAY_MILLIS)}, type = $timeLineType")
    }

    private fun drawFrame(canvas: Canvas){
        //Upper line
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, linePaint)
        //Center line
        canvas.drawLine(0f, height.toFloat()*0.5f, width.toFloat(), height.toFloat()*0.5f, linePaint)
        //Button line
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), linePaint)
    }

    private fun drawPrimaryTimeLineByMonth(canvas: Canvas){
        calendar.set(endYear,endMonth,1,0,0,0)
        calendar.add(Calendar.MONTH,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,1,0,0,0)
        calendar.add(Calendar.MONTH,-1)
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){
            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                0.0f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                height.toFloat()*0.5f,
                linePaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            canvas.drawText(
                millisToYearMonth(actualTime),
                interpolation(startDate,endDate,actualTime + ((days.toFloat()/2.0f)*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                height.toFloat()*0.25f + fontOffsetY,
                textPaint
            )
            actualTime += days * DAY_MILLIS
        }
    }

    private fun drawPrimaryTimeLineByYear(canvas: Canvas){
        calendar.set(endYear,0,1,0,0,0)
        calendar.add(Calendar.YEAR,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,0,1,0,0,0)
        calendar.add(Calendar.YEAR,-1)
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){

            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                0.0f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                height.toFloat()*0.5f,
                linePaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)

            canvas.drawText(
                millisToYear(actualTime),
                interpolation(startDate,endDate,actualTime + (days.toFloat()*0.5f*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                height.toFloat()*0.25f + fontOffsetY,
                textPaint
            )
            actualTime += days * DAY_MILLIS
        }
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
                height.toFloat()*0.5f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                height.toFloat(),
                linePaint
            )

            canvas.drawText(
                millisToDay(actualTime),
                interpolation(startDate,endDate,actualTime + (0.5f*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                height.toFloat()*0.75f + fontOffsetY,
                textPaint
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
                height.toFloat()*0.5f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                height.toFloat(),
                linePaint
            )

            canvas.drawText(
                millisToDay(actualTime),
                interpolation(startDate,endDate,actualTime + (3.5f*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                height.toFloat()*0.75f + fontOffsetY,
                textPaint
            )
            actualTime += 7*DAY_MILLIS
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
                height.toFloat()*0.5f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                height.toFloat(),
                linePaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            canvas.drawText(
                millisToMonth(actualTime),
                interpolation(startDate,endDate,actualTime + ((days.toFloat()/2.0f)*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                height.toFloat()*0.75f + fontOffsetY,
                textPaint
            )
            actualTime += days * DAY_MILLIS
        }
    }



    fun interpolation(startTime : Long, endTime : Long, actualTime : Long) : Float{
        return (actualTime-startTime).toFloat()/(endTime - startTime).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        drawFrame(canvas)
        setTimeLineScale()
        when(timeLineType){
            0 -> {
                drawPrimaryTimeLineByMonth(canvas)
                drawSecondaryTimeLineByDay(canvas)
            }
            1 -> {
                drawPrimaryTimeLineByMonth(canvas)
                drawSecondaryTimeLineByWeek(canvas)
            }
            2 -> {
                drawPrimaryTimeLineByYear(canvas)
                drawSecondaryTimeLineByMonth(canvas)
            }
            else -> {
                drawPrimaryTimeLineByYear(canvas)
                drawSecondaryTimeLineByMonth(canvas)
            }
        }

    }
}