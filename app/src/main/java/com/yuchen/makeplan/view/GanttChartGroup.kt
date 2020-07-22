package com.yuchen.makeplan.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.HOUR_MILLIS
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.ext.toDp
import com.yuchen.makeplan.ext.toPx
import com.yuchen.makeplan.util.TimeUtil
import java.util.*
import kotlin.math.hypot
import kotlin.math.pow


class GanttChartGroup : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val taskHeight = 50.toPx()
    val timeLineHeight = 50.toPx()

    private var taskList : List<Task>? = null

    private var timeLineType: Int = 0

    private var calendar: Calendar = Calendar.getInstance()
    private var startDate: Long = 0L
    private var endDate: Long = 0L

    private var startYear: Int = calendar.get(Calendar.YEAR)
    private var startMonth: Int = calendar.get(Calendar.MONTH)
    private var startDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
    private var startHour: Int = calendar.get(Calendar.HOUR_OF_DAY)

    private var endYear: Int = calendar.get(Calendar.YEAR)
    private var endMonth: Int = calendar.get(Calendar.MONTH)
    private var endDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
    private var endHour: Int = calendar.get(Calendar.HOUR_OF_DAY)

    private var taskSelect = -1

    var dy = 0f

    private val linePaint = Paint().apply {
        strokeWidth = 1.toPx().toFloat()
        color = Color.GRAY
    }

    private val timeLineTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 16.toPx().toFloat()
        textAlign = Paint.Align.CENTER
    }

    private val timeLineBackPaint = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.FILL
    }

    private val taskTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 16.toPx().toFloat()
        textAlign = Paint.Align.LEFT
    }

    private val barPaint = Paint()

    private val taskSelectPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2.toPx().toFloat()
    }

    val fontTaskOffsetY = -(taskTextPaint.fontMetrics.top + taskTextPaint.fontMetrics.bottom)/2
    val fontTimeLineOffsetY = -(timeLineTextPaint.fontMetrics.top + timeLineTextPaint.fontMetrics.bottom)/2

    fun setRange(startTimeMillis: Long, endTimeMillis: Long){
        //Log.d("chenyjzn","s : $startTimeMillis e:$endTimeMillis")
        startDate = startTimeMillis
        calendar.timeInMillis = startTimeMillis
        startYear = calendar.get(Calendar.YEAR)
        startMonth = calendar.get(Calendar.MONTH)
        startDay = calendar.get(Calendar.DAY_OF_MONTH)
        startHour = calendar.get(Calendar.HOUR_OF_DAY)

        endDate = endTimeMillis
        calendar.timeInMillis = endTimeMillis
        endYear = calendar.get(Calendar.YEAR)
        endMonth = calendar.get(Calendar.MONTH)
        endDay= calendar.get(Calendar.DAY_OF_MONTH)
        endHour = calendar.get(Calendar.HOUR_OF_DAY)
        invalidate()
    }

    fun setTaskList(taskList: List<Task>){
        this.taskList = taskList
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

    fun setYPos(dy : Float){
        taskList?.let {
            val allTaskHeight = taskHeight * it.size
            val ganttHeight = height - timeLineHeight
            if (ganttHeight >= allTaskHeight) {
                this.dy = 0f
            }else if (allTaskHeight + this.dy + dy <= ganttHeight){
                this.dy = (ganttHeight - allTaskHeight).toFloat()
            }else if (this.dy + dy >= 0f){
                this.dy = 0f
            } else{
                this.dy += dy
            }
        }
    }

    fun calScale(Scale : Long) : Float{
        return ((Scale.toFloat()/(endDate - startDate).toFloat())*width.toFloat()).toDp()
    }

    fun setTimeLineScale(){
        if (calScale(HOUR_MILLIS)>=25.0f) {
            timeLineType = SCALE_HOUR
        }else if(calScale(6*HOUR_MILLIS)>=25.0f){
            timeLineType = SCALE_6HOUR
        } else if(calScale(DAY_MILLIS)>=25.0f){
            timeLineType = SCALE_DAY
        } else if(calScale(7 * DAY_MILLIS)>=25.0f){
            timeLineType = SCALE_WEEK
        }else if(calScale(28 * DAY_MILLIS)>=25.0f){
            timeLineType = SCALE_MONTH
        }else{
            timeLineType = -1
        }
    }

    fun setTaskSelect(pos:Int){
        taskSelect = pos
    }

    fun interpolation(startTime : Long, endTime : Long, actualTime : Long) : Float{
        return (actualTime-startTime).toFloat()/(endTime - startTime).toFloat()
    }

    private fun drawPrimaryTimeLineByDay(canvas: Canvas){
        calendar.set(endYear,endMonth,endDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,-1)
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){
            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                0.0f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                timeLineHeight.toFloat()*0.5f,
                linePaint
            )

            canvas.drawText(
                TimeUtil.millisToYearMonthDay(actualTime),
                interpolation(startDate,endDate,actualTime + ((DAY_MILLIS.toFloat()/2.0f)).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.25f + fontTimeLineOffsetY,
                timeLineTextPaint
            )
            actualTime += DAY_MILLIS
        }
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
                timeLineHeight.toFloat()*0.5f,
                linePaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            canvas.drawText(
                TimeUtil.millisToYearMonth(actualTime),
                interpolation(startDate,endDate,actualTime + ((days.toFloat()/2.0f)*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.25f + fontTimeLineOffsetY,
                timeLineTextPaint
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
                timeLineHeight.toFloat()*0.5f,
                linePaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)

            canvas.drawText(
                TimeUtil.millisToYear(actualTime),
                interpolation(startDate,endDate,actualTime + (days.toFloat()*0.5f*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.25f + fontTimeLineOffsetY,
                timeLineTextPaint
            )
            actualTime += days * DAY_MILLIS
        }
    }

    private fun drawSecondaryTimeLineByHour(canvas: Canvas){
        calendar.set(endYear,endMonth,endDay,endHour,0,0)
        calendar.add(Calendar.HOUR,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,startHour,0,0)
        calendar.add(Calendar.HOUR,-1)
        var actualTime = calendar.timeInMillis
        while (actualTime <= extendEndTime){
            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                timeLineHeight.toFloat()*0.5f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                timeLineHeight.toFloat(),
                linePaint
            )

            canvas.drawText(
                TimeUtil.millisToHour(actualTime),
                interpolation(startDate,endDate,actualTime + (0.5f*HOUR_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.75f + fontTimeLineOffsetY,
                timeLineTextPaint
            )
            actualTime += HOUR_MILLIS
        }
    }

    private fun drawSecondaryTimeLineBy6Hour(canvas: Canvas){
        calendar.set(endYear,endMonth,endDay,endHour,0,0)
        calendar.add(Calendar.HOUR,6-endHour%6)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,startHour,0,0)
        calendar.add(Calendar.HOUR,-startHour%6)
        var actualTime = calendar.timeInMillis
        while (actualTime <= extendEndTime){
            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                timeLineHeight.toFloat()*0.5f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                timeLineHeight.toFloat(),
                linePaint
            )

            canvas.drawText(
                TimeUtil.millisToHour(actualTime),
                interpolation(startDate,endDate,actualTime + (3.0f*HOUR_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.75f + fontTimeLineOffsetY,
                timeLineTextPaint
            )
            actualTime += 6*HOUR_MILLIS
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
                timeLineHeight.toFloat()*0.5f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                timeLineHeight.toFloat(),
                linePaint
            )

            canvas.drawText(
                TimeUtil.millisToDay(actualTime),
                interpolation(startDate,endDate,actualTime + (0.5f*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.75f + fontTimeLineOffsetY,
                timeLineTextPaint
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
                timeLineHeight.toFloat()*0.5f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                timeLineHeight.toFloat(),
                linePaint
            )

            canvas.drawText(
                TimeUtil.millisToDay(actualTime),
                interpolation(startDate,endDate,actualTime + (3.5f*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.75f + fontTimeLineOffsetY,
                timeLineTextPaint
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
                timeLineHeight.toFloat()*0.5f,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                timeLineHeight.toFloat(),
                linePaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            canvas.drawText(
                TimeUtil.millisToMonth(actualTime),
                interpolation(startDate,endDate,actualTime + ((days.toFloat()/2.0f)*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.75f + fontTimeLineOffsetY,
                timeLineTextPaint
            )
            actualTime += days * DAY_MILLIS
        }
    }

    private fun drawTaskTimeLineByHour(canvas: Canvas, top : Float, bottom : Float){
        calendar.set(endYear,endMonth,endDay,endHour,0,0)
        calendar.add(Calendar.HOUR,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,startHour,0,0)
        calendar.add(Calendar.HOUR,-1)
        var actualTime = calendar.timeInMillis
        while (actualTime <= extendEndTime){
            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                top,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                bottom,
                linePaint
            )
            actualTime += HOUR_MILLIS
        }
    }

    private fun drawTaskTimeLineBy6Hour(canvas: Canvas, top : Float, bottom : Float){
        calendar.set(endYear,endMonth,endDay,endHour,0,0)
        calendar.add(Calendar.HOUR,6-endHour%6)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,startHour,0,0)
        calendar.add(Calendar.HOUR,-startHour%6)
        var actualTime = calendar.timeInMillis
        while (actualTime <= extendEndTime){
            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                top,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                bottom,
                linePaint
            )
            actualTime += 6*HOUR_MILLIS
        }
    }

    private fun drawTaskTimeLineByDay(canvas: Canvas, top : Float, bottom : Float){
        calendar.set(endYear,endMonth,endDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,-1)
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){

            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                top,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                bottom,
                linePaint
            )

            actualTime += DAY_MILLIS
        }
    }

    private fun drawTaskTimeLineByWeek(canvas: Canvas, top : Float, bottom : Float){
        calendar.set(endYear,endMonth,endDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,8-calendar.get(Calendar.DAY_OF_WEEK))
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,1-calendar.get(Calendar.DAY_OF_WEEK))
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){

            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                top,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                bottom,
                linePaint
            )

            actualTime += 7* DAY_MILLIS
        }
    }

    private fun drawTaskTimeLineByMonth(canvas: Canvas, top : Float, bottom : Float){
        calendar.set(endYear,endMonth,1,0,0,0)
        calendar.add(Calendar.MONTH,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,1,0,0,0)
        calendar.add(Calendar.MONTH,-1)
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){
            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                top,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                bottom,
                linePaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            actualTime += days * DAY_MILLIS
        }
    }

    private fun drawTimeLine(canvas: Canvas){
        canvas.drawRect(0f,0f,width.toFloat(),timeLineHeight.toFloat(),timeLineBackPaint)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, linePaint)
        canvas.drawLine(0f, timeLineHeight.toFloat()*0.5f, width.toFloat(), timeLineHeight.toFloat()*0.5f, linePaint)
        canvas.drawLine(0f, timeLineHeight.toFloat(), width.toFloat(), timeLineHeight.toFloat(), linePaint)
        when(timeLineType){
            SCALE_HOUR -> {
                drawPrimaryTimeLineByDay(canvas)
                drawSecondaryTimeLineByHour(canvas)
            }
            SCALE_6HOUR ->{
                drawPrimaryTimeLineByDay(canvas)
                drawSecondaryTimeLineBy6Hour(canvas)
            }
            SCALE_DAY -> {
                drawPrimaryTimeLineByMonth(canvas)
                drawSecondaryTimeLineByDay(canvas)
            }
            SCALE_WEEK -> {
                drawPrimaryTimeLineByMonth(canvas)
                drawSecondaryTimeLineByWeek(canvas)
            }
            SCALE_MONTH -> {
                drawPrimaryTimeLineByYear(canvas)
                drawSecondaryTimeLineByMonth(canvas)
            }
            else -> throw IllegalArgumentException("wrong scale type")
        }
    }

    private fun drawGanttChart(canvas: Canvas) {
        taskList?.let {
            if(it.size > 0){
                for ((index, value) in it.withIndex()){
                    val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
                    val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
                    val top = ((index)*taskHeight).toFloat()+timeLineHeight + dy
                    val bottom = ((index+1)*taskHeight).toFloat()+timeLineHeight + dy
                    when(timeLineType){
                        SCALE_HOUR -> {
                            drawTaskTimeLineByHour(canvas,top,bottom)
                        }
                        SCALE_6HOUR ->{
                            drawTaskTimeLineBy6Hour(canvas,top,bottom)
                        }
                        SCALE_DAY -> {
                            drawTaskTimeLineByDay(canvas,top,bottom)
                        }
                        SCALE_WEEK -> {
                            drawTaskTimeLineByWeek(canvas,top,bottom)
                        }
                        SCALE_MONTH -> {
                            drawTaskTimeLineByMonth(canvas,top,bottom)
                        }
                        else -> throw IllegalArgumentException("wrong scale type")
                    }
                    canvas.drawLine(0f, bottom, width.toFloat(), bottom, linePaint)
                    canvas.drawText(value.name,left,top + taskHeight.toFloat()*0.25f + fontTaskOffsetY, taskTextPaint)
                    barPaint.color = Color.parseColor("#${value.color}")
                    canvas.drawRoundRect(left,top + taskHeight.toFloat()*0.5f,right , bottom,15f,15f, barPaint)
                    barPaint.color = Color.WHITE
                    canvas.drawRect(left,top + taskHeight.toFloat()*0.5f ,left + (right - left)*value.completeRate.toFloat()/100f , bottom,barPaint)
                    if (index == taskSelect){
                        canvas.drawRoundRect(left,top + taskHeight.toFloat()*0.5f,right , bottom,15f,15f, taskSelectPaint)
                    }
//                    Log.d("chenyjzn","Task : $index, left = ${left} , right = ${right}, up: ${top + taskHight.toFloat()*0.5f}, down = ${bottom + this.dy}")
                }
            }
        }
    }

    fun posTaskSelect(x : Float, y : Float) : Int{
        taskList?.let {
            for ((index, value) in it.withIndex()){
                val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
                val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
                val top = ((index)*taskHeight).toFloat() + taskHeight.toFloat()*0.5f + dy+timeLineHeight
                val bottom = ((index+1)*taskHeight).toFloat() + dy+timeLineHeight
                if (x in left..right && y in top..bottom)
                    return index
            }
        }
        return -1
    }

    fun taskSelect(x : Float, y : Float) : Task?{
        taskList?.let {
            for ((index, value) in it.withIndex()){
                val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
                val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
                val top = ((index)*taskHeight).toFloat() + taskHeight.toFloat()*0.5f + dy+timeLineHeight
                val bottom = ((index+1)*taskHeight).toFloat() + dy+timeLineHeight
                if (x in left..right && y in top..bottom)
                    return value
            }
        }
        return null
    }

    private var onEventListener: OnEventListener? = null
    fun setOnEventListener(onEventListener: OnEventListener?) {
        this.onEventListener = onEventListener
    }
    interface OnEventListener {
        fun eventMoveDx(dx : Float, width : Int)
        fun eventZoomDlDr(dl : Float, dr : Float, width : Int)
        fun eventTaskSelect(taskPos: Int)
    }

    var x0 = 0f
    var y0 = 0f
    var x1 = 0f
    var y1 = 0f
    var c = Calendar.getInstance()
    var touchStart = c.timeInMillis
    var touchStatus = TouchMode.NONE

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    x0 = event.x
                    y0 = event.y
                    c = Calendar.getInstance()
                    touchStart = c.timeInMillis
                    touchStatus = TouchMode.CLICK
//                    Log.d("chenyjzn","touch1, x = $x0 , y = $y0")
                    return true
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    x1 = event.getX(1)
                    y1 = event.getY(1)
                    touchStatus = TouchMode.ZOOM
//                    Log.d("chenyjzn","touch2, x = $x1 , y = $y1")
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount == 1) {
                        if (touchStatus == TouchMode.MOVE) {
                            setYPos(event.y - y0)
                            setProjectTimeByDx(event.x - x0, width)
                            onEventListener?.eventMoveDx(event.x - x0, width)
//                            binding.multiGanttTimeLine.setProjectTimeByDx(event.x - x0, v.width)
//                            binding.multiGanttTimeLine.invalidate()
//                            binding.multiGanttChart.invalidate()
                        } else if (touchStatus != TouchMode.NONE && (event.y - y0).pow(2) + (event.x - x0).pow(2) > 6.0f
                        ) {
                            touchStatus = TouchMode.MOVE
                            setYPos(event.y - y0)
                            setProjectTimeByDx(event.x - x0, width)
                            onEventListener?.eventMoveDx(event.x - x0, width)
//                            binding.multiGanttTimeLine.setProjectTimeByDx(event.x - x0, v.width)
//                            binding.multiGanttTimeLine.invalidate()
//                            binding.multiGanttChart.invalidate()
                        }
                        x0 = event.x
                        y0 = event.y
                    } else if (event.pointerCount == 2) {
                        if (touchStatus == TouchMode.ZOOM) {
                            val centerX = (x0 + x1) / 2
                            val centerY = (y0 + y1) / 2
                            val oldXR: Float
                            val oldYR: Float
                            val newXR: Float
                            val nerYR: Float
                            val oldXL: Float
                            val oldYL: Float
                            val newXL: Float
                            val nerYL: Float
                            if (x1 > x0) {
                                oldXR = x1
                                oldYR = y1
                                oldXL = x0
                                oldYL = y0
                                newXR = event.getX(1)
                                nerYR = event.getY(1)
                                newXL = event.getX(0)
                                nerYL = event.getY(0)
                            } else {
                                oldXR = x0
                                oldYR = y0
                                oldXL = x1
                                oldYL = y1
                                newXR = event.getX(0)
                                nerYR = event.getY(0)
                                newXL = event.getX(1)
                                nerYL = event.getY(1)
                            }
                            val dl = hypot(newXL - centerX, nerYL - centerY) - hypot(
                                (oldXL - centerX),
                                (oldYL - centerY)
                            )
                            val dr = hypot(newXR - centerX, nerYR - centerY) - hypot(
                                (oldXR - centerX),
                                (oldYR - centerY)
                            )
//                            Log.d("chenyjzn", "dl = $dl , dr = $dr")
                            setProjectTimeByDlDr(dl, dr, width)
                            onEventListener?.eventZoomDlDr(dl, dr, width)
//                            binding.multiGanttTimeLine.setProjectTimeByDlDr(dl, dr, v.width)
//                            binding.multiGanttTimeLine.invalidate()
//                            binding.multiGanttChart.invalidate()
                            x0 = event.getX(0)
                            x1 = event.getX(1)
                            y0 = event.getY(0)
                            y1 = event.getY(1)
                        }
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (touchStatus == TouchMode.CLICK) {
                        c = Calendar.getInstance()
                        if (c.timeInMillis - touchStart < MAX_CLICK_DURATION) {
//                            Log.d("chenyjzn","Touch up x = ${event.x} , pos y = ${event.y} click")
//                            viewModel.setTaskSelect((v as GanttChart).posTaskSelect(event.x,event.y))
                            onEventListener?.eventTaskSelect(posTaskSelect(event.x,event.y))
                        }
                    }
                    touchStatus = TouchMode.NONE
                    return false
                }
                else -> {
                    return true
                }
            }
        }
        else{
            return true
        }
    }

    override fun onDraw(canvas: Canvas) {
        setTimeLineScale()
        drawGanttChart(canvas)
        drawTimeLine(canvas)
    }

    companion object{
        const val MAX_CLICK_DURATION = 400
        enum class TouchMode {
            CLICK,
            LONG_CLICK,
            MOVE,
            ZOOM,
            NONE
        }
        const val SCALE_HOUR = 0
        const val SCALE_6HOUR = 1
        const val SCALE_DAY = 2
        const val SCALE_WEEK = 3
        const val SCALE_MONTH = 4
    }
}