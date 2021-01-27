package com.example.snake

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Toast
import stanford.androidlib.graphics.GCanvas
import stanford.androidlib.graphics.GColor
import stanford.androidlib.graphics.GRect
import stanford.androidlib.graphics.GSprite
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList
import kotlin.collections.HashMap



class GameCanvas(context: Context?, attrs: AttributeSet) : GCanvas(context, attrs) {

    private var walls = ArrayList<GSprite>();
    private var snake = ArrayDeque<GSprite>()
    private var apples = ArrayList<GSprite>()
    private var score = 0
    private var directionMap = HashMap<GSprite, Int>()
    //constants
    companion object{
        private const val UP = 0
        private const val LEFT = 1
        private const val DOWN = 2
        private const val RIGHT = 3
        private const val WALLTHICKNESS = 80f
        private const val BODYTHICKNESS = 50f
        private const val APPLETHICKNESS = BODYTHICKNESS
        private const val XSWIPE = 200f
        private const val YSWIPE = 200f
        private const val Cord = BODYTHICKNESS
        private const val FPS = 3
        private const val SCOREINCREMENT = 5
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun init() {
        this.backgroundColor = GColor.BLACK
        addWalls()
        addSnake()

        animate(FPS){
            tick()
        }
        var x1:Float = 0f
        var x2:Float = 0f
        var y1:Float = 0f
        var y2:Float = 0f
        setOnTouchListener{ _,event ->

            if(event.action == MotionEvent.ACTION_DOWN){
                x1 = event.x
                y1 = event.y
            }
            if(event.action == MotionEvent.ACTION_UP){
                x2 = event.x
                y2 = event.y
                if(x1>x2 && abs(x1-x2)> XSWIPE){

                    directionMap[snake[0]] = LEFT
                }else if(x1<x2 && abs(x1-x2)> XSWIPE) {
                    directionMap[snake[0]] = RIGHT
                }else if(y1>y2 && abs(y1-y2)> YSWIPE){
                    directionMap[snake[0]] = UP
                }else if(y2>y1 && abs(y1-y2)> YSWIPE){
                    directionMap[snake[0]] = DOWN
                }
            }

            true

        }
    }
    private fun tick(){
        addApples()
        checkwallcollision()
        checkapplecollision()
        if(snake.size == 1){
            if(directionMap[snake[0]] == UP){
                snake[0].y-=Cord
            }else if(directionMap[snake[0]] == LEFT){
                snake[0].x-=Cord
            }else if(directionMap[snake[0]] == DOWN){
                snake[0].y+=Cord
            }else if(directionMap[snake[0]] == RIGHT){
                snake[0].x+=Cord
            }
        }else{
            remove(snake[snake.size -1])
            snake.removeLast()
            addMoreBlock(directionMap[snake[0]])
        }
        updateAll()
    }

    private fun checkapplecollision(){
        for(i in 0..(apples.size -1)){
            val apple = apples[i]
            if(snake[0].collidesWith(apple)){
                remove(apples[i])
                apples.removeAt(i)
                score+=SCOREINCREMENT
                addMoreBlock(directionMap[snake[0]])
                break
            }
        }
    }


    private fun addMoreBlock(dir: Int?) {
        val head = snake[0]
        var x = 0f
        var y = 0f;
       if(dir == DOWN){
            x = head.x
            y = head.y + BODYTHICKNESS
       }else if(dir == UP){
            x = head.x
            y = head.y- BODYTHICKNESS
       }else if(dir == RIGHT){
            x = head.x + BODYTHICKNESS
            y = head.y
       }else if(dir == LEFT){
            x = head.x - BODYTHICKNESS
            y = head.y
       }

        val block = GSprite(GRect(BODYTHICKNESS, BODYTHICKNESS).setFillColor(GColor.WHITE))
        block.x = x
        block.y = y
        snake.addFirst(block)
        if(dir == UP){
            directionMap[snake[0]] = UP
        }else if(dir == DOWN){
            directionMap[snake[0]] = DOWN
        }else if(dir == LEFT){
            directionMap[snake[0]] = LEFT
        }else{
            directionMap[snake[0]] = RIGHT
        }
        add(block)
    }
    private fun checkwallcollision() {
        for(wall in walls){
            if(wall.collidesWith(snake[0])){
                animationStop()
                val bob = AlertDialog.Builder(context)
                bob.setTitle("Game Over")
                bob.setMessage("Your Score: $score")
                bob.setPositiveButton("ok"){ _, _ ->
                    (context as Activity).finish()
                }
                bob.create().show()
            }
        }
    } // checks the collision of head with a wall

    private fun addApples(){
        if(apples.size == 3)
                return
        if(apples.size > 3)
            Toast.makeText(context, "something wrong", Toast.LENGTH_SHORT).show()
        while(apples.size < 3){
            val apple = GSprite(GRect(APPLETHICKNESS, APPLETHICKNESS).setFillColor(GColor.RED))
            val maxX = (this.width - WALLTHICKNESS).toInt() - APPLETHICKNESS.toInt()
            val minX = (WALLTHICKNESS).toInt()+ APPLETHICKNESS.toInt()
            val maxY = (this.height - WALLTHICKNESS).toInt()- APPLETHICKNESS.toInt()
            val minY = (WALLTHICKNESS).toInt()+ APPLETHICKNESS.toInt()
            apple.x = (Random().nextInt(maxX - minX + 1) + minX).toFloat()
            apple.y = (Random().nextInt(maxY - minY + 1) + minY).toFloat()
            apples.add((apple))
            add(apple)
        }
    } // checks if there are less than 3 apples at any time and if not adds one

    private fun addSnake() {
        val head = GSprite(GRect(BODYTHICKNESS, BODYTHICKNESS).setFillColor(GColor.WHITE))
        head.x = this.width.toFloat()/2
        head.y = this.height.toFloat()/2
        snake.addLast(head)
        directionMap[snake[0]] = UP
        add(head)
    }

    private fun addWalls() {
        //right wall
        val rightwall = GSprite(GRect(WALLTHICKNESS, this.height.toFloat()).setFillColor(GColor.RED))
        rightwall.rightX = this.width.toFloat()
        rightwall.y = 0f;
        walls.add(rightwall)
        add(rightwall)

        //left wall
        val leftwall = GSprite(GRect(WALLTHICKNESS, this.height.toFloat()).setFillColor(GColor.RED))
        leftwall.x = 0f
        walls.add(leftwall)
        add(leftwall)

        //bottom wall
        val bottomWall = GSprite(GRect(this.width.toFloat(), WALLTHICKNESS).setFillColor(GColor.RED))
        bottomWall.x = 0f
        bottomWall.bottomY = this.height.toFloat()
        walls.add(bottomWall)
        add(bottomWall)

        val topWall = GSprite(GRect(this.width.toFloat(), WALLTHICKNESS).setFillColor(GColor.RED))
        topWall.x =0f
        topWall.y = 0f
        walls.add(topWall)
        add(topWall)
    } // adds the four walls

}