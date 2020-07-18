package com.yuchen.makeplan.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.data.Project
import com.yuchen.makeplan.data.Task
import com.yuchen.makeplan.ext.toDp
import com.yuchen.makeplan.ext.toPx
import java.util.*

class GanttChart : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val taskHight = 50.toPx()

//    private var project : Project? = null

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

    private var taskSelect = -1

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
    }

    fun setTaskList(taskList: List<Task>){
        this.taskList = taskList
    }

//    fun setProject(project: Project){
//        this.project = project
//
//        calendar.timeInMillis = project.startTimeMillis
//
//        startDate = project.startTimeMillis
//        startYear = calendar.get(Calendar.YEAR)
//        startMonth = calendar.get(Calendar.MONTH)
//        startDay = calendar.get(Calendar.DAY_OF_MONTH)
//
//        endDate = project.endTimeMillis
//        calendar.timeInMillis = project.endTimeMillis
//        endYear = calendar.get(Calendar.YEAR)
//        endMonth = calendar.get(Calendar.MONTH)
//        endDay= calendar.get(Calendar.DAY_OF_MONTH)
//    }

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

//        project?.let {
//            val allTaskHigh = taskHight * it.taskList.size
//            if (height >= allTaskHigh) {
//                this.dy = 0f
//            }else if (allTaskHigh + this.dy + dy <= height){
//                this.dy = (height - allTaskHigh).toFloat()
//            }else if (this.dy + dy >= 0f){
//                this.dy = 0f
//            } else{
//                this.dy += dy
//            }
//        }
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

    fun setTaskSelect(pos:Int){
        taskSelect = pos
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
                    if (index == taskSelect){
                        canvas.drawRoundRect(left,top + taskHight.toFloat()*0.5f + this.dy,right , bottom + this.dy,15f,15f, taskSelectPaint)
                    }
//                    Log.d("chenyjzn","Task : $index, left = ${left} , right = ${right}, up: ${top + taskHight.toFloat()*0.5f}, down = ${bottom + this.dy}")
                }
            }
        }

//        project?.let {project ->
//            if(project.taskList.size > 0){
//                val allTaskHigh = (taskHight * project.taskList.size).toFloat()
//                when(timeLineType){
//                    0 -> {
//                        drawSecondaryTimeLineByDay(canvas,allTaskHigh)
//                    }
//                    1 -> {
//                        drawSecondaryTimeLineByWeek(canvas,allTaskHigh)
//                    }
//                    2 -> {
//                        drawSecondaryTimeLineByMonth(canvas,allTaskHigh)
//                    }
//                    else -> {
//                        drawSecondaryTimeLineByMonth(canvas,allTaskHigh)
//                    }
//                }
//                for ((index, value) in project.taskList.withIndex()){
//                    val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
//                    val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
//                    val top = ((index)*taskHight).toFloat()
//                    val bottom = ((index+1)*taskHight).toFloat()
//                    canvas.drawLine(0f, bottom + this.dy, width.toFloat(), bottom+this.dy, linePaint)
//                    canvas.drawText(value.name,left,top + taskHight.toFloat()*0.25f + fontOffsetY + this.dy, textPaint)
//                    barPaint.color = Color.parseColor("#${value.color}")
//                    canvas.drawRoundRect(left,top + taskHight.toFloat()*0.5f + this.dy,right , bottom + this.dy,15f,15f, barPaint)
//                    barPaint.color = Color.WHITE
//                    canvas.drawRect(left,top + taskHight.toFloat()*0.5f +this.dy,left + (right - left)*value.completeRate.toFloat()/100f , bottom + this.dy,barPaint)
//                    if (index == taskSelect){
//                        canvas.drawRoundRect(left,top + taskHight.toFloat()*0.5f + this.dy,right , bottom + this.dy,15f,15f, taskSelectPaint)
//                    }
////                    Log.d("chenyjzn","Task : $index, left = ${left} , right = ${right}, up: ${top + taskHight.toFloat()*0.5f}, down = ${bottom + this.dy}")
//                }
//            }
//        }
    }

    fun posTaskSelect(x : Float, y : Float) : Int{
        taskList?.let {
            for ((index, value) in it.withIndex()){
                val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
                val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
                val top = ((index)*taskHight).toFloat() + taskHight.toFloat()*0.5f + dy
                val bottom = ((index+1)*taskHight).toFloat() + dy
                if (x in left..right && y in top..bottom)
                    return index
            }
        }

//        project?.let {
//            for ((index, value) in it.taskList.withIndex()){
//                val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
//                val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
//                val top = ((index)*taskHight).toFloat() + taskHight.toFloat()*0.5f + dy
//                val bottom = ((index+1)*taskHight).toFloat() + dy
//                if (x in left..right && y in top..bottom)
//                    return index
//            }
//        }
        return -1
    }

    override fun onDraw(canvas: Canvas) {
        setTimeLineScale()
        drawGanttChart(canvas)
    }
}