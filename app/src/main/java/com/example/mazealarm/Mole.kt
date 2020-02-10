package com.example.mazealarm
import kotlin.random.Random

class Mole(mazeSize :Int) {
    private val startX = getRandomOdd(mazeSize) //開始x位置
    private val startY = getRandomOdd(mazeSize) //開始y位置
    private var currentX = startX //現在のモグラさんのx位置。
    private var currentY = startY //現在のモグラさんのy位置

    fun setCurrentPosition(digPosition: Array<Int>) {
        this.currentY = digPosition[0]
        this.currentX = digPosition[1]
    }

    fun getStartPosition():Array<Int> {
        return arrayOf(startY, startX)
    }

    fun getCurrentPosition(): Array<Int>{
        return arrayOf(currentY, currentX) //現在のモグラさんの位置を返す
    }

    fun digHole(mazeField: Array<Array<Int>>, mazeSize:Int): Array<Int>{
        var i = 0
        while(i < 1000){
            var digX = 0 //モグラの掘るx方向
            var digY = 0 //モグラの掘るy距離
            var direction = Random.nextInt(4) //0:上, 1:左, 2:右, 3:下

            when(direction) {
                0 -> {
                    digY = -2
                    digX = 0
                }
                1 -> {
                    digY = 0
                    digX = -2
                }
                2 -> {
                    digY = 0
                    digX = 2
                }
                3 ->{
                    digY = 2
                    digX = 0
                }
            }
            if(currentY + digY < 0 || currentY + digY > mazeSize - 1 || currentX + digX < 0 || currentX + digX > mazeSize - 1) continue
            if (mazeField[currentY + digY][currentX + digX] == 0) {
                return arrayOf(currentY + digY, currentX + digX)
            }
            else{
                do {
                    currentY = getRandomOdd(mazeSize)
                    currentX = getRandomOdd(mazeSize)
                }while(mazeField[currentY][currentX] != 1)
            }
            i++
        }
        return arrayOf(-1)
    }

    fun getRandomOdd(num: Int): Int {
        var odd: Int
        while(true) {
            odd = Random.nextInt(num)
            if (odd % 2 != 0) return odd
        }
    }
}