package com.yuchen.makeplan

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.ext.toPx

@BindingAdapter("colorString","taskColor")
fun bindColorByColorCode(view: View, colorString: String?, taskColor : String?) {
    colorString?.let { color ->
        view.background = ShapeDrawable(object : Shape() {
            override fun draw(canvas: Canvas, paint: Paint) {
                paint.color = Color.parseColor(color)
                paint.style = Paint.Style.FILL
                canvas.drawCircle(this.width/2,this.height/2,18.toPx().toFloat(),paint)
                if (taskColor == colorString){
                    paint.color = Color.BLACK
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 2.toPx().toFloat()
                    canvas.drawCircle(this.width/2,this.height/2,17.toPx().toFloat(),paint)
                    paint.color = Color.WHITE
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 2.toPx().toFloat()
                    canvas.drawCircle(this.width/2,this.height/2,19.toPx().toFloat(),paint)

                }
            }
        })
    }
}

@BindingAdapter("projectName","membersCount")
fun bindProjectNameAndCount(view: TextView, projectName: String?, membersCount : Int?) {
    projectName?.let {
        if (membersCount == 0 || membersCount == null)
            view.text = projectName
        else
            view.text = "$projectName ($membersCount)"
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

@BindingAdapter("users","pos")
fun bindBatchImageCircle(imgView: ImageView, users: List<User>?, pos : Int) {
    if (users == null || users.isEmpty()){
        imgView.visibility = View.GONE
        return
    }
    users?.let {
        if (pos < users.size){
            val imgUri = users[pos].photoUrl.toUri().buildUpon().scheme("https").build()
            Glide.with(imgView.context)
                .load(imgUri)
                .apply(RequestOptions().circleCrop())
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_account_circle_black_24dp)
                        .error(R.drawable.ic_account_circle_black_24dp)
                )
                .into(imgView)
        }else{
            imgView.visibility = View.GONE
        }
    }
}