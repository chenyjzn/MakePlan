package com.yuchen.makeplan

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yuchen.makeplan.ext.toPx

@BindingAdapter("colorString","taskColor")
fun bindColorByColorCode(view: View, colorString: String?, taskColor : String?) {
    colorString?.let { color ->
        view.background = ShapeDrawable(object : Shape() {
            override fun draw(canvas: Canvas, paint: Paint) {
                paint.color = Color.parseColor("#$color")
                paint.style = Paint.Style.FILL
                canvas.drawCircle(this.width/2,this.height/2,this.width/2,paint)
                if (taskColor == colorString){
                    Log.d("chenyjzn", "color = taskColor")
                    paint.color = Color.BLACK
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 2.toPx().toFloat()
                    canvas.drawCircle(this.width/2,this.height/2,this.width/2-paint.strokeWidth,paint)
                }
            }
        })
    }
}

@BindingAdapter("image")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions().placeholder(R.drawable.ic_broken_image_black_24dp)
                    .error(R.drawable.ic_broken_image_black_24dp)
            )
            .into(imgView)
    }
}

@BindingAdapter("imageCircle")
fun bindImageCircle(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(RequestOptions().circleCrop())
            .apply(
                RequestOptions().placeholder(R.drawable.ic_account_circle_black_24dp)
                    .error(R.drawable.ic_account_circle_black_24dp)
            )
            .into(imgView)
    }
}