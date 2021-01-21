package com.example.lunarlander

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import stanford.androidlib.graphics.*
import stanford.androidlib.graphics.SimpleBitmapConstants.Companion.context
import stanford.androidlib.util.RandomGenerator
import java.util.Random

class LanderCanvas(context:Context, attrs:AttributeSet):GCanvas(context, attrs){
    private lateinit var rocket:GSprite
    private lateinit var moonSurface:GSprite
    private var cnt:Int = 0
    private var score:Int = 0
    private var asteroids:ArrayList<GSprite> = ArrayList<GSprite>()
    override fun init() {
        backgroundColor = GColor.BLACK
        //adding moon surface
        var surfaceImage = BitmapFactory.decodeResource(resources, R.drawable.moonsurface)
        surfaceImage = surfaceImage.scale(this.width/1f, this.height/5f)
        moonSurface = GSprite(surfaceImage)
        moonSurface.setCollisionMarginTop((moonSurface.height/2)+80f)
        moonSurface.bottomY = this.height.toFloat()
        add(moonSurface)

        //adding rocket
        var rocketImage = BitmapFactory.decodeResource(resources, R.drawable.rocket)
        rocketImage = rocketImage.scaleToWidth(this.width/3f)
         rocket = GSprite(rocketImage)
        rocket.accelerationX = 0f
        rocket.accelerationY = 1f
        rocket.setCollisionMarginBottom(10f)
        rocket.setCollisionMarginRight(45f)
        add(rocket)


        setOnTouchListener{ _,event ->
            val x = event.x
            val y = event.y
            if(rocket.isMoving){
                if(event.action == MotionEvent.ACTION_DOWN){
                    rocket.accelerationY = -0.8f
                }else if(event.action == MotionEvent.ACTION_UP){
                    rocket.accelerationY = 1f
                }
            }
            true

        }

    }

    private fun tick() {
        rocket.update()
        if(!rocket.isMoving)
                return
        score++
        for(asteroid in asteroids){
            asteroid.update()
        }
        if(cnt%30 == 0){
            randomAsteroid()
        }
        if(rocket.collidesWith(moonSurface)){
            stopthegame()
        }
        else{
            if(asteroids.size!=0){
                for(asteroid in asteroids){
                    if(rocket.collidesWith(asteroid)){
                        stopthegame()
                    }
                }
            }
        }
        cnt++
    }
    private fun stopthegame(){
        rocket.stop()
        Toast.makeText(context, "Game Over, Score: $score", Toast.LENGTH_SHORT).show()
    }
    private fun randomAsteroid(){
        val randomY = RandomGenerator.getInstance().nextInt(0, this.height)
        val x = this.width
        var asteroidImage = BitmapFactory.decodeResource(resources, R.drawable.asteroid)
        asteroidImage = asteroidImage.scaleToWidth(this.width/10f)
       val asteroid = GSprite(asteroidImage)
       asteroid.rightX = x.toFloat()
        asteroid.y = randomY.toFloat()
        asteroid.accelerationY = 0f
        asteroid.accelerationX = -0.3f
        asteroid.setCollisionMargin( asteroid.width/2f)
        asteroids.add(asteroid)
        add(asteroid)
    }
    fun move(){
        animate(30){
            tick()
        }
    }
}