package com.yuchen.makeplan.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.ext.toDp
import com.yuchen.makeplan.ext.toPx
import java.util.*
import kotlin.math.hypot
import kotlin.math.pow

class MultiGanttChart : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val taskHight = 50.toPx()

    private var taskList : List<Task>? = null

    private var timeLineType: Int = 0

    private var calendar: Calendar = Calendar.getInstance()

    private var startDate: Long = 0L
    private var endDate: Long = 0L

    private var startYear: Int = calendar.get(Calendar.YEAR)
    private var startMonth: Int = calendar.get(Calendar.MONTH)
    private var startDay: Int = calendar.get(Calendar.DAY_OF_MONTH)

    private var endYear: Int = calendar.get(Calendar.YEAR)
    private var endMonth: Int = calendar.get(Calendar.MONTH)
    private var endDay: Int = calendar.get(Calendar.DAY_OF_MONTH)

    private var taskSelect:Task? = null

    var dy = 0f

    private val linePaint = Paint().apply {
        strokeWidth = 1.toPx().toFloat()
        color = Color.GRAY
    }

    private val textPaint = Paint().apply {
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

    val fontOffsetY = -(textPaint.fontMetrics.top + textPaint.fontMetrics.bottom)/2

    fun setRange(startTimeMillis: Long, endTimeMillis: Long){
        startDate = startTimeMillis
        calendar.timeInMillis = startTimeMillis
        startYear = calendar.get(Calendar.YEAR)
        startMonth = calendar.get(Calendar.MONTH)
        startDay = calendar.get(Calendar.DAY_OF_MONTH)

        endDate = endTimeMillis
        calendar.timeInMillis = endTimeMillis
        endYear = calendar.get(Calendar.YEAR)
        endMonth = calendar.get(Calendar.MONTH)
        endDay= calendar.get(Calendar.DAY_OF_MONTH)
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
        //Log.d("chenyjzn","allheight = ${taskHight * project!!.taskList!!.size} height = $height, oldDy = ${this.dy}, newDy = $dy")
        taskList?.let {
            val allTaskHigh = taskHight * it.size
            if (height >= allTaskHigh) {
                this.dy = 0f
            }else if (allTaskHigh + this.dy + dy <= height){
                this.dy = (height - allTaskHigh).toFloat()
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
        if (calScale(DAY_MILLIS)>=25.0f){
            timeLineType = 0
        } else if(calScale(7 * DAY_MILLIS)>=25.0f){
            timeLineType = 1
        }else if(calScale(28 * DAY_MILLIS)>=25.0f){
            timeLineType = 3
        }else{
            timeLineType = 4
        }
    }

    fun setTaskSelect(task:Task?){
        taskSelect = task
    }

    fun interpolation(startTime : Long, endTime : Long, actualTime : Long) : Float{
        return (actualTime-startTime).toFloat()/(endTime - startTime).toFloat()
    }

    private fun drawSecondaryTimeLineByDay(canvas: Canvas, allTaskHigh : Float){
        calendar.set(endYear,endMonth,endDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,-1)
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){

            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                0f+this.dy,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                allTaskHigh+this.dy,
                linePaint
            )

            actualTime += DAY_MILLIS
        }
    }

    private fun drawSecondaryTimeLineByWeek(canvas: Canvas, allTaskHigh : Float){
        calendar.set(endYear,endMonth,endDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,8-calendar.get(Calendar.DAY_OF_WEEK))
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,startDay,0,0,0)
        calendar.add(Calendar.DAY_OF_YEAR,1-calendar.get(Calendar.DAY_OF_WEEK))
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){

            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                0f+this.dy,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                allTaskHigh+this.dy,
                linePaint
            )

            actualTime += 7* DAY_MILLIS
        }
    }

    private fun drawSecondaryTimeLineByMonth(canvas: Canvas, allTaskHigh : Float){
        calendar.set(endYear,endMonth,1,0,0,0)
        calendar.add(Calendar.MONTH,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,1,0,0,0)
        calendar.add(Calendar.MONTH,-1)
        var actualTime = calendar.timeInMillis

        while (actualTime <= extendEndTime){
            canvas.drawLine(
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                0f+this.dy,
                interpolation(startDate,endDate,actualTime)*width.toFloat(),
                allTaskHigh+this.dy,
                linePaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            actualTime += days * DAY_MILLIS
        }
    }


    private fun drawGanttChart(canvas: Canvas) {
        taskList?.let {
            if(it.size > 0){
                val allTaskHigh = (taskHight * it.size).toFloat()
                when(timeLineType){
                    0 -> {
                        drawSecondaryTimeLineByDay(canvas,allTaskHigh)
                    }
                    1 -> {
                        drawSecondaryTimeLineByWeek(canvas,allTaskHigh)
                    }
                    2 -> {
                        drawSecondaryTimeLineByMonth(canvas,allTaskHigh)
                    }
                    else -> {
                        drawSecondaryTimeLineByMonth(canvas,allTaskHigh)
                    }
                }
                for ((index, value) in it.withIndex()){
                    val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
                    val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
                    val top = ((index)*taskHight).toFloat()
                    val bottom = ((index+1)*taskHight).toFloat()
                    canvas.drawLine(0f, bottom + this.dy, width.toFloat(), bottom+this.dy, linePaint)
                    canvas.drawText(value.name,left,top + taskHight.toFloat()*0.25f + fontOffsetY + this.dy, textPaint)
                    barPaint.color = Color.parseColor("#${value.color}")
                    canvas.drawRoundRect(left,top + taskHight.toFloat()*0.5f + this.dy,right , bottom + this.dy,15f,15f, barPaint)
                    barPaint.color = Color.WHITE
                    canvas.drawRect(left,top + taskHight.toFloat()*0.5f +this.dy,left + (right - left)*value.completeRate.toFloat()/100f , bottom + this.dy,barPaint)
                    taskSelect?.let {
                        if (it.firebaseId == value.firebaseId)
                            canvas.drawRoundRect(left,top + taskHight.toFloat()*0.5f + this.dy,right , bottom + this.dy,15f,15f, taskSelectPaint)
                    }
//                    Log.d("chenyjzn","Task : $index, left = ${left} , right = ${right}, up: ${top + taskHight.toFloat()*0.5f}, down = ${bottom + this.dy}")
                }
            }
        }
    }

    fun taskSelect(x : Float, y : Float) : Task?{
        taskList?.let {
            for ((index, value) in it.withIndex()){
                val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
                val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
                val top = ((index)*taskHight).toFloat() + taskHight.toFloat()*0.5f + dy
                val bottom = ((index+1)*taskHight).toFloat() + dy
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
        fun eventTaskSelect(task: Task?)
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
                        } else if (touchStatus != TouchMode.NONE && (event.y - y0).pow(2) + (event.x - x0).pow(2) > 6.0f) {
                            touchStatus = TouchMode.MOVE
                            setYPos(event.y - y0)
                            setProjectTimeByDx(event.x - x0, width)
                            onEventListener?.eventMoveDx(event.x - x0, width)
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
                            onEventListener?.eventTaskSelect(taskSelect(event.x,event.y))
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
    }
}