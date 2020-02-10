package com.example.mazealarm

class Maze(val mazeSize: Int) {
    private val mazeField = Array(mazeSize, { Array(mazeSize, {0})})

    fun getMazeField(): Array<Array<Int>>{
        return mazeField //迷路の２次元配列取得
    }

    fun setStartHole(digPosition :Array<Int>){
        mazeField[digPosition[0]][digPosition[1]] = 2 //mapにしたい
    }

    fun setGoalHole(digPosition :Array<Int>) {
        if (digPosition[0] > mazeSize / 2) {
            digPosition[0] = 1
        }
        else {
            digPosition[0] = mazeSize - 2
        }
        if (digPosition[1] > mazeSize / 2) {
            digPosition[1] = 1
        }
        else {
            digPosition[1] = mazeSize - 2
        }
        mazeField[digPosition[0]][digPosition[1]] = 3 //mapにしたい
    }

    fun diggedHole(digPosition :Array<Int>, molePosition :Array<Int>): Array<Int>{
        if(digPosition[0] < molePosition[0]) {
            mazeField[digPosition[0] + 1][digPosition[1]]= 1
        }
        if(digPosition[0] > molePosition[0]) {
            mazeField[digPosition[0] - 1][digPosition[1]]= 1
        }
        if(digPosition[1] < molePosition[1]) {
            mazeField[digPosition[0]][digPosition[1] + 1]= 1
        }
        if(digPosition[1] > molePosition[1]) {
            mazeField[digPosition[0]][digPosition[1] - 1]= 1
        }
        mazeField[digPosition[0]][digPosition[1]] = 1 //mapにしたい
        return digPosition
    }

    fun generateMazeField(){ //コマンドラインに迷路配列表示
        for (i in 0..mazeSize-1) {
            for (j in 0.. mazeSize-1) {
                print("${mazeField[i][j]} ")
            }
            println(" ")
        }
    }
}