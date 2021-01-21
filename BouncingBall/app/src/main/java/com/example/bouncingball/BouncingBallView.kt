package com.example.bouncingball

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import stanford.androidlib.graphics.GCanvas
import stanford.androidlib.graphics.GColor
import stanford.androidlib.graphics.GOval
import java.util.*

class BouncingBallView(context: Context, attrs: AttributeSet)
    : GCanvas(context, attrs){
    private val SIZE = 100f;
    private var ball = GOval(0f,0f,SIZE, SIZE)
    private val RAINDROP_DY = 10f
    private var dx = 10f
    private var dy = 10f
    private val rainWid = 50f
    private var cnt = 0
    override fun init() {
        ball.fillColor = GColor.PURPLE
        add(ball)
        animate(120){
            move()
        }
    }

    private fun addDrops(){
        val rand = Random().nextInt((width - rainWid).toInt())
        val rainDrop = GOval(rand.toFloat(), -1f, rainWid, rainWid)
        rainDrop.fillColor = GColor.BLUE
        add(rainDrop)
    }
    fun move(){
        if(cnt%40 == 0){
            cnt = 0;
            addDrops()
        }
        cnt++

        ball.moveBy(dx, dy)
        if(ball.rightX >= width || ball.x <= 0){
            dx = -dx
        }
        if(ball.bottomY >= height || ball.y <=0){
            dy=-dy
        }
        for(all in this){
            if(all == ball)
                    continue
            all.moveBy(0f, RAINDROP_DY)
        }
    }
}