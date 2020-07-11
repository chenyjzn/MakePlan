package com.yuchen.makeplan.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class GanttTaskTextView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var posProportion : Float = 0f

    fun setPosProportion(projectStart : Long, projectEnd : Long, taskStart : Long){
        posProportion = (taskStart-projectStart).toFloat()/(projectEnd - projectStart).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        x = (this.parent as View).width.toFloat() * posProportion
        super.onDraw(canvas)
    }
}