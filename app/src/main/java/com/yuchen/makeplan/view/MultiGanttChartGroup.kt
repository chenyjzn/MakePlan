package com.yuchen.makeplan.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.HOUR_MILLIS
import com.yuchen.makeplan.MINUTE_MILLIS
import com.yuchen.makeplan.R
import com.yuchen.makeplan.data.MultiTask
import com.yuchen.makeplan.ext.toDp
import com.yuchen.makeplan.ext.toPx
import com.yuchen.makeplan.util.TimeUtil
import com.yuchen.makeplan.util.TimeUtil.StampToDate
import java.util.*
import kotlin.math.hypot
import kotlin.math.pow

class MultiGanttChartGroup : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val primaryTextPadding = 4.toPx()
    private val toolBarTextPadding = 4.toPx()
    private val taskHeight = 60.toPx()
    private val timeLineHeight = 50.toPx()
    private val taskControl = 40.toPx()
    private val toolBarHeight = 20.toPx()
    private val extraBottomSpace = 20.toPx()

    //Colors
    private val colorTimeLineAxis = resources.getColor(R.color.my_gray_100)
    private val colorTimeLineText = resources.getColor(R.color.my_gray_240)
    private val colorTimeLineBack = resources.getColor(R.color.my_gray_60)
    private val colorGanttLineVertical = resources.getColor(R.color.my_gray_90)
    private val colorGanttLineHorizontal = resources.getColor(R.color.my_gray_90)
    private val colorGanttText = resources.getColor(R.color.my_gray_240)
    private val colorGanttBack = resources.getColor(R.color.my_gray_45)
    private val colorGanttShortSelect = resources.getColor(R.color.blue_gray_500)
    private val colorGanttLongSelect = resources.getColor(R.color.blue_gray_700)
    private val colorToolBarText = resources.getColor(R.color.blue_gray_900)
    private val colorToolBarBack = resources.getColor(R.color.yellow_600)
    private val colorToolBarStroke = resources.getColor(R.color.my_gray_100)

    private var taskList : MutableList<MultiTask>? = null

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

    private var taskSelectPos = -1
    private var taskSelectValue : MultiTask? = null

    private var taskLongSelect = -1
    private var taskLongYPos = 0f

    private var colorList1 : List<String> = listOf()
    private var colorList2 : List<String> = listOf()

    private var dy = 0f

    private var taskActionTimeScale : Long = 0L

    fun setTaskActionTimeScale(type: Int){
        when(type){
            0 -> taskActionTimeScale = DAY_MILLIS
            1 -> taskActionTimeScale = HOUR_MILLIS
            2 -> taskActionTimeScale = 15 * MINUTE_MILLIS
            3 -> taskActionTimeScale = 5 * MINUTE_MILLIS
        }
    }

    fun setColorList(colorList1 : List<String>,colorList2 : List<String> ){
        this.colorList1 = colorList1
        this.colorList2 = colorList2
    }

    private val timeLineAxisPaint = Paint().apply {
        color = colorTimeLineAxis
        strokeWidth = 1.toPx().toFloat()
    }

    private val timeLine1TextPaint = Paint().apply {
        color = colorTimeLineText
        textSize = 16.toPx().toFloat()
        textAlign = Paint.Align.CENTER
    }

    private val timeLine2TextPaint = Paint().apply {
        color = colorTimeLineText
        textSize = 16.toPx().toFloat()
        textAlign = Paint.Align.CENTER
    }

    private val timeLineBackPaint = Paint().apply {
        color = colorTimeLineBack
        style = Paint.Style.FILL
    }

    private val ganttBackPaint = Paint().apply {
        color = colorGanttBack
        style = Paint.Style.FILL
    }

    private val ganttLineVerticalPaint = Paint().apply {
        color = colorGanttLineVertical
        strokeWidth = 1.toPx().toFloat()
    }
    private val ganttLineHorizontalPaint = Paint().apply {
        color = colorGanttLineHorizontal
        strokeWidth = 1.toPx().toFloat()
    }

    private val ganttTextPaint = Paint().apply {
        color = colorGanttText
        textSize = 18.toPx().toFloat()
        textAlign = Paint.Align.LEFT
    }

    private val ganttShortSelectPaint = Paint().apply {
        color = colorGanttShortSelect
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 2.toPx().toFloat()
    }

    private val ganttLongSelectPaint = Paint().apply {
        color = colorGanttLongSelect
        style = Paint.Style.FILL
    }

    private val toolBarTextPaint = Paint().apply {
        color = colorToolBarText
        textSize = 15.toPx().toFloat()
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val toolBarBackPaint = Paint().apply {
        color = colorToolBarBack
        style = Paint.Style.FILL
    }

    private val toolBarStrokePaint = Paint().apply {
        color = colorToolBarStroke
        style = Paint.Style.STROKE
        strokeWidth = 1.toPx().toFloat()
    }

    private val barPaint = Paint()

    val fontTaskOffsetY = -(ganttTextPaint.fontMetrics.top + ganttTextPaint.fontMetrics.bottom)/2
    val fontTimeLineOffsetY = -(timeLine2TextPaint.fontMetrics.top + timeLine2TextPaint.fontMetrics.bottom)/2
    val fontToolBarOffsetY = -(toolBarTextPaint.fontMetrics.top + toolBarTextPaint.fontMetrics.bottom)/2

    fun setRange(startTimeMillis: Long, endTimeMillis: Long){
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

    fun setTaskList(taskList: List<MultiTask>){
        this.taskList = taskList.toMutableList()
    }

    fun setProjectTimeByDx(dx : Float, width : Int) : Pair<Long,Long>{
        var timeOffset = ((endDate - startDate).toFloat()*dx/width.toFloat()).toLong()
        return startDate - timeOffset to endDate - timeOffset
    }

    fun checkProjectBound(){
        if((endDate - startDate)>300 * DAY_MILLIS) {
            endDate = startDate + 300 * DAY_MILLIS
        } else if((endDate - startDate)< 4 * HOUR_MILLIS) {
            endDate = startDate + 4 * HOUR_MILLIS
        }
    }

    fun setProjectTimeByDlDr(dl : Float, dr : Float, width : Int) : Pair<Long,Long> {
        var timeOffsetl = ((endDate - startDate).toFloat() * dl / width.toFloat()).toLong()
        var timeOffsetr = ((endDate - startDate).toFloat() * dr / width.toFloat()).toLong()
        if((endDate - timeOffsetr - startDate - timeOffsetl)>=300 * DAY_MILLIS) {

            return startDate+(endDate-startDate)/2 - 150* DAY_MILLIS to startDate+(endDate-startDate)/2 + 150* DAY_MILLIS
        } else if((endDate - timeOffsetr - startDate - timeOffsetl)<= 4 * HOUR_MILLIS) {
            return startDate+(endDate-startDate)/2 - 2* HOUR_MILLIS to startDate+(endDate-startDate)/2 + 2* HOUR_MILLIS
        } else
            return startDate + timeOffsetl to endDate - timeOffsetr
    }

    fun setYPos(dy : Float){
        taskList?.let {
            val allTaskHeight = taskHeight * it.size
            val ganttHeight = height - timeLineHeight - toolBarHeight - extraBottomSpace
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

    fun calScale(Scale : Long,start : Long, end:Long) : Float{
        return ((Scale.toFloat()/(end - start).toFloat())*width.toFloat()).toDp()
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
        }else if(calScale(28 * DAY_MILLIS)>=35.0f){
            timeLineType = SCALE_MONTH
        }else{
            timeLineType = -1
        }
        Log.d("chenyjzn","cal scale s=${StampToDate(startDate)}, e=${StampToDate(endDate)}, type = $timeLineType")
    }

    fun setTaskPosSelect(pos:Int){
        taskSelectPos = pos
    }

    fun setTaskValueSelect(task:MultiTask?){
        taskSelectValue = task
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

        var prePos = interpolation(startDate,endDate,actualTime)*width.toFloat()
        var curPos : Float
        canvas.drawLine(
            prePos,
            0.0f,
            prePos,
            timeLineHeight.toFloat()*0.5f,
            timeLineAxisPaint
        )

        while (actualTime <= extendEndTime){
            val text = TimeUtil.millisToYearMonthDay(actualTime)
            actualTime += DAY_MILLIS
            curPos = interpolation(startDate,endDate,actualTime)*width.toFloat()
            canvas.drawLine(
                curPos,
                0.0f,
                curPos,
                timeLineHeight.toFloat()*0.5f,
                timeLineAxisPaint
            )
            val halfTextWidth = timeLine2TextPaint.measureText(text)/2.0f + primaryTextPadding.toFloat()
            var textXPos : Float
            if (prePos< 0f && curPos> width.toFloat()){
                timeLine1TextPaint.textAlign = Paint.Align.CENTER
                textXPos = width*0.5f
            } else if (prePos< 0f && curPos<= width.toFloat()){
                if ((curPos)/2.0f + halfTextWidth >= curPos){
                    timeLine1TextPaint.textAlign = Paint.Align.RIGHT
                    textXPos = curPos - primaryTextPadding
                }else {
                    timeLine1TextPaint.textAlign = Paint.Align.CENTER
                    textXPos = (curPos) / 2.0f
                }
            }else if (prePos>= 0f && curPos> width.toFloat()){
                if ((prePos + width.toFloat())/2.0f - halfTextWidth <= prePos){
                    timeLine1TextPaint.textAlign = Paint.Align.LEFT
                    textXPos = prePos + primaryTextPadding
                }else{
                    timeLine1TextPaint.textAlign = Paint.Align.CENTER
                    textXPos = (prePos + width.toFloat())/2.0f
                }
            }else{
                textXPos = (prePos + curPos)/2.0f
                timeLine1TextPaint.textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                text,
                textXPos,
                timeLineHeight.toFloat()*0.25f + fontTimeLineOffsetY,
                timeLine1TextPaint
            )
            prePos = curPos
        }
    }

    private fun drawPrimaryTimeLineByMonth(canvas: Canvas){
        calendar.set(endYear,endMonth,1,0,0,0)
        calendar.add(Calendar.MONTH,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,startMonth,1,0,0,0)
        calendar.add(Calendar.MONTH,-1)
        var actualTime = calendar.timeInMillis

        var prePos = interpolation(startDate,endDate,actualTime)*width.toFloat()
        var curPos : Float

        canvas.drawLine(
            prePos,
            0.0f,
            prePos,
            timeLineHeight.toFloat()*0.5f,
            timeLineAxisPaint
        )

        while (actualTime <= extendEndTime){
            val text = TimeUtil.millisToYearMonth(actualTime)

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            actualTime += days * DAY_MILLIS

            curPos = interpolation(startDate,endDate,actualTime)*width.toFloat()

            canvas.drawLine(
                curPos,
                0.0f,
                curPos,
                timeLineHeight.toFloat()*0.5f,
                timeLineAxisPaint
            )

            val halfTextWidth = timeLine2TextPaint.measureText(text)/2.0f + primaryTextPadding.toFloat()
            var textXPos : Float
            if (prePos< 0f && curPos> width.toFloat()){
                timeLine1TextPaint.textAlign = Paint.Align.CENTER
                textXPos = width*0.5f
            } else if (prePos< 0f && curPos<= width.toFloat()){
                if ((curPos)/2.0f + halfTextWidth >= curPos){
                    timeLine1TextPaint.textAlign = Paint.Align.RIGHT
                    textXPos = curPos - primaryTextPadding
                }else {
                    timeLine1TextPaint.textAlign = Paint.Align.CENTER
                    textXPos = (curPos) / 2.0f
                }
            }else if (prePos>= 0f && curPos> width.toFloat()){
                if ((prePos + width.toFloat())/2.0f - halfTextWidth <= prePos){
                    timeLine1TextPaint.textAlign = Paint.Align.LEFT
                    textXPos = prePos + primaryTextPadding
                }else{
                    timeLine1TextPaint.textAlign = Paint.Align.CENTER
                    textXPos = (prePos + width.toFloat())/2.0f
                }
            }else{
                textXPos = (prePos + curPos)/2.0f
                timeLine1TextPaint.textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                text,
                textXPos,
                timeLineHeight.toFloat()*0.25f + fontTimeLineOffsetY,
                timeLine1TextPaint
            )
            prePos = curPos
        }
    }

    private fun drawPrimaryTimeLineByYear(canvas: Canvas){
        calendar.set(endYear,0,1,0,0,0)
        calendar.add(Calendar.YEAR,1)
        val extendEndTime = calendar.timeInMillis

        calendar.set(startYear,0,1,0,0,0)
        calendar.add(Calendar.YEAR,-1)
        var actualTime = calendar.timeInMillis

        var prePos = interpolation(startDate,endDate,actualTime)*width.toFloat()
        var curPos : Float

        canvas.drawLine(
            prePos,
            0.0f,
            prePos,
            timeLineHeight.toFloat()*0.5f,
            timeLineAxisPaint
        )

        while (actualTime <= extendEndTime){
            val text = TimeUtil.millisToYear(actualTime)

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
            actualTime += days * DAY_MILLIS

            curPos = interpolation(startDate,endDate,actualTime)*width.toFloat()

            canvas.drawLine(
                curPos,
                0.0f,
                curPos,
                timeLineHeight.toFloat()*0.5f,
                timeLineAxisPaint
            )

            val halfTextWidth = timeLine2TextPaint.measureText(text)/2.0f + primaryTextPadding.toFloat()
            var textXPos : Float
            if (prePos< 0f && curPos> width.toFloat()){
                timeLine1TextPaint.textAlign = Paint.Align.CENTER
                textXPos = width*0.5f
            } else if (prePos< 0f && curPos<= width.toFloat()){
                if ((curPos)/2.0f + halfTextWidth >= curPos){
                    timeLine1TextPaint.textAlign = Paint.Align.RIGHT
                    textXPos = curPos - primaryTextPadding
                }else {
                    timeLine1TextPaint.textAlign = Paint.Align.CENTER
                    textXPos = (curPos) / 2.0f
                }
            }else if (prePos>= 0f && curPos> width.toFloat()){
                if ((prePos + width.toFloat())/2.0f - halfTextWidth <= prePos){
                    timeLine1TextPaint.textAlign = Paint.Align.LEFT
                    textXPos = prePos + primaryTextPadding
                }else{
                    timeLine1TextPaint.textAlign = Paint.Align.CENTER
                    textXPos = (prePos + width.toFloat())/2.0f
                }
            }else{
                textXPos = (prePos + curPos)/2.0f
                timeLine1TextPaint.textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                text,
                textXPos,
                timeLineHeight.toFloat()*0.25f + fontTimeLineOffsetY,
                timeLine1TextPaint
            )
            prePos = curPos
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
                timeLineAxisPaint
            )

            canvas.drawText(
                TimeUtil.millisToHour(actualTime),
                interpolation(startDate,endDate,actualTime + (0.5f*HOUR_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.75f + fontTimeLineOffsetY,
                timeLine2TextPaint
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
                timeLineAxisPaint
            )

            canvas.drawText(
                TimeUtil.millisToHour(actualTime),
                interpolation(startDate,endDate,actualTime + (3.0f*HOUR_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.75f + fontTimeLineOffsetY,
                timeLine2TextPaint
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
                timeLineAxisPaint
            )

            canvas.drawText(
                TimeUtil.millisToDay(actualTime),
                interpolation(startDate,endDate,actualTime + (0.5f*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.75f + fontTimeLineOffsetY,
                timeLine2TextPaint
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
                timeLineAxisPaint
            )

            canvas.drawText(
                TimeUtil.millisToDay(actualTime),
                interpolation(startDate,endDate,actualTime + (3.5f*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.75f + fontTimeLineOffsetY,
                timeLine2TextPaint
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
                timeLineAxisPaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            canvas.drawText(
                TimeUtil.millisToMonth(actualTime),
                interpolation(startDate,endDate,actualTime + ((days.toFloat()/2.0f)*DAY_MILLIS.toFloat()).toLong())*width.toFloat(),
                timeLineHeight.toFloat()*0.75f + fontTimeLineOffsetY,
                timeLine2TextPaint
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
                ganttLineVerticalPaint
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
                ganttLineVerticalPaint
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
                ganttLineVerticalPaint
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
                ganttLineVerticalPaint
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
                ganttLineVerticalPaint
            )

            calendar.timeInMillis = actualTime
            val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            actualTime += days * DAY_MILLIS
        }
    }

    private fun drawTimeLine(canvas: Canvas){
        canvas.drawRect(0f,0f,width.toFloat(),timeLineHeight.toFloat(),timeLineBackPaint)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, timeLineAxisPaint)
        canvas.drawLine(0f, timeLineHeight.toFloat()*0.5f, width.toFloat(), timeLineHeight.toFloat()*0.5f, timeLineAxisPaint)
        canvas.drawLine(0f, timeLineHeight.toFloat(), width.toFloat(), timeLineHeight.toFloat(), timeLineAxisPaint)
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
        canvas.drawRect(0f,timeLineHeight.toFloat(),width.toFloat(),height.toFloat(),ganttBackPaint)
        taskList?.let {
            if(it.size > 0){
                for ((index, value) in it.withIndex()){
                    if (index!=taskLongSelect) {
                        val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
                        val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
                        val top = ((index)*taskHeight).toFloat()+timeLineHeight + dy + toolBarHeight
                        val bottom = ((index+1)*taskHeight).toFloat()+timeLineHeight + dy + toolBarHeight
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
                        canvas.drawLine(0f, top, width.toFloat(), top, ganttLineHorizontalPaint)
                        canvas.drawLine(0f, bottom, width.toFloat(), bottom, ganttLineHorizontalPaint)
                        val text = "${value.name} | ${value.completeRate}%"
                        canvas.drawText(text,left,top + taskHeight.toFloat()*0.25f + fontTaskOffsetY, ganttTextPaint)
                        if (value == taskSelectValue){
                            canvas.drawRoundRect(left - taskControl,top + taskHeight.toFloat()*0.5f,right + taskControl, bottom- 0.1f*taskHeight,15f,15f, ganttShortSelectPaint)
                        }
                        barPaint.color = Color.parseColor(colorList1[value.color])
                        canvas.drawRoundRect(left,top + taskHeight.toFloat()*0.5f,right , bottom - 0.1f*taskHeight,15f,15f, barPaint)
                        barPaint.color = Color.parseColor(colorList2[value.color])
                        canvas.drawRoundRect(left,top + taskHeight.toFloat()*0.5f,left + (right - left)*value.completeRate.toFloat()/100f , bottom - 0.1f*taskHeight,15f,15f,barPaint)
                    }
                }
                if (taskLongSelect!=-1){
                    val left = interpolation(startDate,endDate,it[taskLongSelect].startTimeMillis)*width.toFloat()
                    val right = interpolation(startDate,endDate,it[taskLongSelect].endTimeMillis)*width.toFloat()
                    val top = taskLongYPos - taskHeight/2
                    val bottom = taskLongYPos + taskHeight/2
                    canvas.drawRect(0f,top,width.toFloat(),bottom,ganttLongSelectPaint)
//                    when(timeLineType){
//                        SCALE_HOUR -> {
//                            drawTaskTimeLineByHour(canvas,top,bottom)
//                        }
//                        SCALE_6HOUR ->{
//                            drawTaskTimeLineBy6Hour(canvas,top,bottom)
//                        }
//                        SCALE_DAY -> {
//                            drawTaskTimeLineByDay(canvas,top,bottom)
//                        }
//                        SCALE_WEEK -> {
//                            drawTaskTimeLineByWeek(canvas,top,bottom)
//                        }
//                        SCALE_MONTH -> {
//                            drawTaskTimeLineByMonth(canvas,top,bottom)
//                        }
//                        else -> throw IllegalArgumentException("wrong scale type")
//                    }
                    canvas.drawLine(0f, top, width.toFloat(), top, ganttLineHorizontalPaint)
                    canvas.drawLine(0f, bottom, width.toFloat(), bottom, ganttLineHorizontalPaint)
                    val text = "${it[taskLongSelect].name} | ${it[taskLongSelect].completeRate}%"
                    canvas.drawText(text,left,top + taskHeight.toFloat()*0.25f + fontTaskOffsetY, ganttTextPaint)
                    barPaint.color = Color.parseColor(colorList1[it[taskLongSelect].color])
                    canvas.drawRoundRect(left,top + taskHeight.toFloat()*0.5f,right , bottom - 0.1f*taskHeight,15f,15f, barPaint)
                    barPaint.color = Color.parseColor(colorList2[it[taskLongSelect].color])
                    canvas.drawRoundRect(left,top + taskHeight.toFloat()*0.5f,left + (right - left)*it[taskLongSelect].completeRate.toFloat()/100f , bottom - 0.1f*taskHeight,15f,15f,barPaint)
                }
            }
        }
    }

    private fun drawToolBar(canvas: Canvas){
        canvas.drawRect(0f,timeLineHeight.toFloat(),width.toFloat(),timeLineHeight.toFloat() + toolBarHeight,toolBarBackPaint)
        canvas.drawRect(0f,timeLineHeight.toFloat(),width.toFloat(),timeLineHeight.toFloat() + toolBarHeight, toolBarStrokePaint)
        taskSelectValue?.let {
            toolBarTextPaint.textAlign= Paint.Align.LEFT
            var text = TimeUtil.millisToGanttToolBarTime(it.startTimeMillis)
            canvas.drawText(text,0f + toolBarTextPadding,timeLineHeight + toolBarHeight.toFloat()/2f + fontToolBarOffsetY, toolBarTextPaint)
            toolBarTextPaint.textAlign= Paint.Align.CENTER
            text = TimeUtil.timeDurationToString(it.startTimeMillis,it.endTimeMillis)
            canvas.drawText(text,width.toFloat()/2f,timeLineHeight + toolBarHeight.toFloat()/2f + fontToolBarOffsetY, toolBarTextPaint)
            toolBarTextPaint.textAlign= Paint.Align.RIGHT
            text = TimeUtil.millisToGanttToolBarTime(it.endTimeMillis)
            canvas.drawText(text,width.toFloat() - toolBarTextPadding,timeLineHeight + toolBarHeight.toFloat()/2f + fontToolBarOffsetY, toolBarTextPaint)
        }
    }

    fun checkTaskModeTouchPos(x : Float, y : Float) : TouchMode {
        taskList?.let {
            for ((index, value) in it.withIndex()){
//                Log.d("chenyjzn","multi project check task ${value == taskSelectValue} ${value.firebaseId == taskSelectValue?.firebaseId} ${value.firebaseId} , ${taskSelectValue?.firebaseId}")
                val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
                val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
                val top = ((index)*taskHeight).toFloat() + taskHeight.toFloat()*0.6f + dy+timeLineHeight +toolBarHeight
                val bottom = ((index+1)*taskHeight).toFloat() + dy+timeLineHeight + toolBarHeight
                if (x in left..right && y in top..bottom && value == taskSelectValue){
                    return TouchMode.TASK_PRE_MOVE
                }else if(x in left - taskControl..left && y in top..bottom && value == taskSelectValue){
                    return TouchMode.TASK_PRE_LEFT
                }else if(x in  right..right+taskControl && y in top..bottom && value == taskSelectValue){
                    return TouchMode.TASK_PRE_RIGHT
                }
            }
        }
        return TouchMode.CLICK
    }

    fun getTaskSelect(x : Float, y : Float) : Pair<Int,MultiTask?> {
        taskList?.let {
            for ((index, value) in it.withIndex()){
                val left = interpolation(startDate,endDate,value.startTimeMillis)*width.toFloat()
                val right = interpolation(startDate,endDate,value.endTimeMillis)*width.toFloat()
                val top = ((index)*taskHeight).toFloat() + taskHeight.toFloat()*0.6f + dy+timeLineHeight +toolBarHeight
                val bottom = ((index+1)*taskHeight).toFloat() + dy+timeLineHeight +toolBarHeight
                if (x in left..right && y in top..bottom)
                    return index to value
            }
        }
        return -1 to null
    }

    fun getTaskLongSelect(y : Float) : Int {
        taskList?.let {
            for ((index, value) in it.withIndex()){
                val top = ((index)*taskHeight).toFloat()+dy+timeLineHeight + toolBarHeight
                val bottom = ((index+1)*taskHeight).toFloat()+dy+timeLineHeight + toolBarHeight
                if (y in top..bottom)
                    return index
            }
        }
        return -1
    }

    fun checkTaskSwapByYPos(yPos : Float){
        taskList?.let {list->
            for ((index, value) in list.withIndex()){
                if (index == taskLongSelect)
                    continue
                val top = ((index)*taskHeight).toFloat()+dy+timeLineHeight + toolBarHeight
                val bottom = ((index+1)*taskHeight).toFloat()+dy+timeLineHeight + toolBarHeight
                if (yPos in top..bottom){
                    list[taskLongSelect] = list[index].also {
                        list[index] = list[taskLongSelect]
                    }
                    taskLongSelect = index
                }
            }
        }
    }

    private var onEventListener: OnEventListener? = null
    fun setOnEventListener(onEventListener: OnEventListener?) {
        this.onEventListener = onEventListener
    }
    interface OnEventListener {
        fun eventChartTime(startTimeMillis: Long,endTimeMillis: Long)
        fun eventMoveDx(dx : Float, width : Int)
        fun eventZoomDlDr(dl : Float, dr : Float, width : Int)
        fun eventTaskSelect(taskPos: Int, taskValue : MultiTask?)
        fun eventTaskModify(taskPos: Int, task : MultiTask)
        fun eventTaskSwap(task : MutableList<MultiTask>)
    }

    var x0 = 0f
    var y0 = 0f
    var x1 = 0f
    var y1 = 0f
    var touchStatus = TouchMode.NONE

    fun setTaskTimeOffsetByDx(dx : Float, width : Int) : Long{
        return ((endDate - startDate).toFloat()*dx/width.toFloat()).toLong()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (taskSelectValue == null) {
                        x0 = event.x
                        y0 = event.y
                        touchStatus = TouchMode.CLICK
                        handler.postDelayed(Runnable {
                            if (touchStatus == TouchMode.CLICK && taskSelectValue == null){
                                touchStatus = TouchMode.LONG_CLICK
                                taskLongSelect = getTaskLongSelect(y0)
                                taskLongYPos = y0
                                invalidate()
                            }
                        }, MAX_CLICK_DURATION)
                        return true
                    }else{
                        x0 = event.x
                        y0 = event.y
                        touchStatus = checkTaskModeTouchPos(x0,y0)
//                        handler.postDelayed(Runnable {
//                            if (touchStatus == TouchMode.CLICK)
//                                touchStatus = TouchMode.NONE
//                        }, MAX_CLICK_DURATION)
                        return true
                    }
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    if (touchStatus == TouchMode.CLICK || touchStatus == TouchMode.MOVE || touchStatus == TouchMode.TASK_PRE_MOVE || touchStatus == TouchMode.TASK_PRE_LEFT|| touchStatus == TouchMode.TASK_PRE_RIGHT) {
                        x0 = event.getX(0)
                        y0 = event.getY(0)
                        x1 = event.getX(1)
                        y1 = event.getY(1)
                        touchStatus = TouchMode.ZOOM
                        return true
                    }else {
                        if (touchStatus == TouchMode.TASK_MOVE || touchStatus == TouchMode.TASK_LEFT || touchStatus == TouchMode.TASK_RIGHT) {
                            taskSelectValue?.let {
                                onEventListener?.eventTaskModify(taskSelectPos, it)
                            }
                        } else if (touchStatus == TouchMode.LONG_CLICK) {
                            taskLongYPos = 0f
                            taskLongSelect = -1
                            taskList?.let {
                                onEventListener?.eventTaskSwap(it)
                            }
                        }
                        touchStatus = TouchMode.NONE
                        return false
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount == 1) {
                        if (touchStatus == TouchMode.MOVE) {
                            setYPos(event.y - y0)
                            val newTime = setProjectTimeByDx(event.x - x0, width)
                            setRange(newTime.first,newTime.second)
                            onEventListener?.eventChartTime(newTime.first,newTime.second)
                            x0 = event.x
                            y0 = event.y
                        } else if (touchStatus == TouchMode.CLICK && (event.y - y0).pow(2) + (event.x - x0).pow(2) > 6.0f) {
                            touchStatus = TouchMode.MOVE
                            setYPos(event.y - y0)
                            val newTime =  setProjectTimeByDx(event.x - x0, width)
                            setRange(newTime.first,newTime.second)
                            onEventListener?.eventChartTime(newTime.first,newTime.second)
                            x0 = event.x
                            y0 = event.y
                        } else if (touchStatus == TouchMode.TASK_PRE_MOVE && (event.y - y0).pow(2) + (event.x - x0).pow(2) > 6.0f) {
                            touchStatus = TouchMode.TASK_MOVE
                            val timeOffset =  setTaskTimeOffsetByDx(event.x - x0, width)
                            if(timeOffset/taskActionTimeScale != 0L){
                                taskSelectValue?.let {
                                    it.startTimeMillis += taskActionTimeScale*(timeOffset/taskActionTimeScale)
                                    it.endTimeMillis += taskActionTimeScale*(timeOffset/taskActionTimeScale)
                                    invalidate()
                                }
                                x0 = event.x
                                y0 = event.y
                            }
                        }else if(touchStatus == TouchMode.TASK_MOVE){
                            val timeOffset =  setTaskTimeOffsetByDx(event.x - x0, width)
                            if(timeOffset/taskActionTimeScale != 0L){
                                taskSelectValue?.let {
                                    it.startTimeMillis += taskActionTimeScale*(timeOffset/taskActionTimeScale)
                                    it.endTimeMillis += taskActionTimeScale*(timeOffset/taskActionTimeScale)
                                    invalidate()
                                }
                                x0 = event.x
                                y0 = event.y
                            }
                        }else if (touchStatus == TouchMode.TASK_PRE_LEFT && (event.y - y0).pow(2) + (event.x - x0).pow(2) > 6.0f){
                            touchStatus = TouchMode.TASK_LEFT
                            val timeOffset =  setTaskTimeOffsetByDx(event.x - x0, width)
                            if(timeOffset/taskActionTimeScale != 0L) {
                                taskSelectValue?.let {
                                    if (it.startTimeMillis + timeOffset >= it.endTimeMillis) {
                                        it.startTimeMillis = it.endTimeMillis
                                    } else {
                                        it.startTimeMillis += taskActionTimeScale*(timeOffset/taskActionTimeScale)
                                    }
                                    invalidate()
                                }
                                x0 = event.x
                                y0 = event.y
                            }
                        }else if (touchStatus == TouchMode.TASK_LEFT){
                            val timeOffset =  setTaskTimeOffsetByDx(event.x - x0, width)
                            if(timeOffset/taskActionTimeScale != 0L) {
                                taskSelectValue?.let {
                                    if (it.startTimeMillis + timeOffset >= it.endTimeMillis) {
                                        it.startTimeMillis = it.endTimeMillis
                                    } else {
                                        it.startTimeMillis += taskActionTimeScale*(timeOffset/taskActionTimeScale)
                                    }
                                    invalidate()
                                }
                                x0 = event.x
                                y0 = event.y
                            }
                        }else if (touchStatus == TouchMode.TASK_PRE_RIGHT && (event.y - y0).pow(2) + (event.x - x0).pow(2) > 6.0f){
                            touchStatus = TouchMode.TASK_RIGHT
                            val timeOffset =  setTaskTimeOffsetByDx(event.x - x0, width)
                            if(timeOffset/taskActionTimeScale != 0L) {
                                taskSelectValue?.let {
                                    if (it.endTimeMillis + timeOffset <= it.startTimeMillis) {
                                        it.endTimeMillis = it.startTimeMillis
                                    } else {
                                        it.endTimeMillis += taskActionTimeScale*(timeOffset/taskActionTimeScale)
                                    }
                                    invalidate()
                                }
                                x0 = event.x
                                y0 = event.y
                            }
                        }else if (touchStatus == TouchMode.TASK_RIGHT){
                            val timeOffset =  setTaskTimeOffsetByDx(event.x - x0, width)
                            if(timeOffset/taskActionTimeScale != 0L) {
                                taskSelectValue?.let {
                                    if (it.endTimeMillis + timeOffset <= it.startTimeMillis) {
                                        it.endTimeMillis = it.startTimeMillis
                                    } else {
                                        it.endTimeMillis += taskActionTimeScale*(timeOffset/taskActionTimeScale)
                                    }
                                    invalidate()
                                }
                                x0 = event.x
                                y0 = event.y
                            }
                        }else if (touchStatus == TouchMode.LONG_CLICK){
                            taskLongYPos = event.y
                            checkTaskSwapByYPos(event.y)
                            invalidate()
                            x0 = event.x
                            y0 = event.y
                        }
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
                            val newTime = setProjectTimeByDlDr(dl, dr, width)
                            setRange(newTime.first,newTime.second)
                            onEventListener?.eventChartTime(newTime.first,newTime.second)
                            x0 = event.getX(0)
                            x1 = event.getX(1)
                            y0 = event.getY(0)
                            y1 = event.getY(1)
                        }
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (touchStatus == TouchMode.CLICK || touchStatus == TouchMode.TASK_PRE_MOVE) {
                        val taskPair = getTaskSelect(event.x,event.y)
                        onEventListener?.eventTaskSelect(taskPair.first,taskPair.second)
                    }else if (touchStatus == TouchMode.TASK_MOVE || touchStatus == TouchMode.TASK_LEFT || touchStatus == TouchMode.TASK_RIGHT){
                        taskSelectValue?.let {
                            onEventListener?.eventTaskModify(taskSelectPos,it)
                        }
                    }else if(touchStatus == TouchMode.LONG_CLICK){
                        taskLongYPos = 0f
                        taskLongSelect = -1
                        taskList?.let {
                            onEventListener?.eventTaskSwap(it)
                        }
                        invalidate()
                    }
                    touchStatus = TouchMode.NONE
                    return false
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    if (touchStatus == TouchMode.ZOOM){
                        touchStatus = TouchMode.MOVE
                        if(event.actionIndex == 0){
                            y0 = event.getY(1)
                            x0 = event.getX(1)
                        }else{
                            y0 = event.getY(0)
                            x0 = event.getX(0)
                        }
                    }
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
        checkProjectBound()
        setTimeLineScale()
        drawGanttChart(canvas)
        drawTimeLine(canvas)
        drawToolBar(canvas)
    }

    companion object{
        const val MAX_CLICK_DURATION = 400L
        enum class TouchMode {
            CLICK,
            LONG_CLICK,
            MOVE,
            ZOOM,
            NONE,
            TASK_LEFT,
            TASK_PRE_LEFT,
            TASK_RIGHT,
            TASK_PRE_RIGHT,
            TASK_PRE_MOVE,
            TASK_MOVE
        }
        const val SCALE_HOUR = 0
        const val SCALE_6HOUR = 1
        const val SCALE_DAY = 2
        const val SCALE_WEEK = 3
        const val SCALE_MONTH = 4
    }
}