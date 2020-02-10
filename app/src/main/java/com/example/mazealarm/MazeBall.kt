package com.example.mazealarm

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_maze_ball.*

class MazeBall : AppCompatActivity(), SensorEventListener, SurfaceHolder.Callback {
    private val tag = "Black holes"
    private val matrixSize = 16
    private var mgValues = FloatArray(3)
    private var acValues = FloatArray(3)
    private var startTime: Long = 0
    private var surfaceWidth = 0
    private var surfaceHeight = 0
    private var ballX = 0f
    private var ballY = 0f
    private var isGoal = false

    private val mazeSize = 19 //迷路の縦横の大きさ
    private val mazeBoxSize: Float = 60f
    private val mazeXPosition: Float = -25f
    private val mazeYPosition: Float = 100f
    private val mole = Mole(mazeSize)
    private val maze = Maze(mazeSize)

    private val radius = (mazeBoxSize / 2) * 0.8f
    private var ballXPositionCheck: Boolean = true
    private var ballYPositionCheck: Boolean = true
    private var ballXCollidedPosition: Float = ballX
    private var ballYCollidedPosition: Float = ballY
    private var ballStartCheck: Boolean = true

    private lateinit var player: MediaPlayer

    override fun onSensorChanged(event: SensorEvent?) {
        val inR = FloatArray(matrixSize)
        val outR = FloatArray(matrixSize)
        val l = FloatArray(matrixSize)
        val orValues = FloatArray(3)

        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> acValues = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> mgValues = event.values.clone()
        }

        SensorManager.getRotationMatrix(inR, l, acValues, mgValues)

        SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, outR)
        SensorManager.getOrientation(outR, orValues)

        val pitch = rad2Deg(orValues[1])
        val roll = rad2Deg(orValues[2])
        Log.v(tag, "pitch${pitch}")
        Log.v(tag, "roll${roll}")
        if (!isGoal) {
            drawGameBoard(pitch, roll)
        }
    }

    private fun drawGameBoard(pitch: Int, roll: Int) {

        val canvas = surfaceView.holder.lockCanvas()
        val paint = Paint()

        val mazeField= maze.getMazeField()
        canvas.drawColor(Color.WHITE)

        if(ballXPositionCheck) {
            ballX -= 0.4f * roll
        }
        if (ballYPositionCheck) {
            ballY += 0.4f * pitch
        }


        if (ballX < 0) {
            ballX = radius
        } else if (ballX > surfaceWidth) {
            ballX = surfaceWidth - radius
        }

        if (ballY + radius < 0) {
            //isGoal = true
        } else if (ballY + radius > surfaceHeight) {
            ballY = surfaceHeight - radius
        }

        for (i in 0..mazeSize -1) {
            for (j in 0..mazeSize - 1) {
                when (mazeField[i][j]) {
                    1 -> paint.color = Color.WHITE
                    2 -> {
                        paint.color = Color.GREEN
                        if(ballStartCheck) {
                            ballY = mazeYPosition + (mazeBoxSize * (i + 1) + mazeBoxSize)
                            ballX = mazeXPosition + (mazeBoxSize * (j + 1) + mazeBoxSize)
                        }
                        ballStartCheck = false
                    }
                    3 -> {
                        paint.color = Color.MAGENTA
                        if (mazeXPosition + (mazeBoxSize * (j + 1)) <= ballX  && ballX  <= mazeXPosition + (mazeBoxSize * (j + 1) + mazeBoxSize)) {
                            if (mazeYPosition + (mazeBoxSize * (i + 1)) <= ballY && ballY <= mazeYPosition + (mazeBoxSize * (i + 1) + mazeBoxSize)) {
                                isGoal =true
                            }
                        }
                    }
                    else -> paint.color = Color.BLACK
                }
                canvas.drawRect( mazeXPosition + (mazeBoxSize * (j + 1)), mazeYPosition + (mazeBoxSize * (i + 1)),  mazeXPosition + (mazeBoxSize * (j + 1) + mazeBoxSize),  mazeYPosition + (mazeBoxSize * (i + 1) + mazeBoxSize), paint)

                if (mazeField[i][j] == 0) {
                    if (mazeXPosition + (mazeBoxSize * (j + 1)) <= ballX  && ballX  <= mazeXPosition + (mazeBoxSize * (j + 1) + mazeBoxSize)) {
                        if (ballY - radius < mazeYPosition + (mazeBoxSize * (i + 1) + mazeBoxSize) && ballY + radius > mazeYPosition + (mazeBoxSize * (i + 1) + mazeBoxSize)) {
                            if(ballYPositionCheck) {
                                ballYCollidedPosition = mazeYPosition + (mazeBoxSize * (i + 1) + mazeBoxSize) + radius
                                ballYPositionCheck = false
                            }
                            ballY = ballYCollidedPosition
                        }else {
                            ballYPositionCheck = true
                        }

                        if(ballY + radius > mazeYPosition + (mazeBoxSize * (i + 1)) && ballY - radius < mazeYPosition + (mazeBoxSize * (i + 1))) {
                            if(ballYPositionCheck) {
                                ballYCollidedPosition = mazeYPosition + (mazeBoxSize * (i + 1)) - radius
                                ballYPositionCheck = false
                            }
                            ballY = ballYCollidedPosition
                        }else {
                            ballYPositionCheck = true
                        }
                    }

                    if (mazeYPosition + (mazeBoxSize * (i + 1)) <= ballY && ballY <= mazeYPosition + (mazeBoxSize * (i + 1) + mazeBoxSize)) {
                        if (ballX - radius < mazeXPosition + (mazeBoxSize * (j + 1) + mazeBoxSize) && ballX + radius > mazeXPosition + (mazeBoxSize * (j + 1) + mazeBoxSize)) {
                            if(ballXPositionCheck) {
                                ballXCollidedPosition = mazeXPosition + (mazeBoxSize * (j + 1) + mazeBoxSize) + radius
                                ballXPositionCheck = false
                            }
                            ballX = ballXCollidedPosition
                        }else {
                            ballXPositionCheck = true
                        }
                        if (ballX + radius > mazeXPosition + (mazeBoxSize * (j + 1)) && ballX - radius < mazeXPosition + (mazeBoxSize * (j + 1))) {
                            if(ballXPositionCheck) {
                                ballXCollidedPosition =
                                    mazeXPosition + (mazeBoxSize * (j + 1)) - radius
                                ballXPositionCheck = false
                            }
                            ballX = ballXCollidedPosition
                        }else {
                            ballXPositionCheck = true
                        }
                    }
                }else {
                    ballXPositionCheck = true
                    ballYPositionCheck = true
                }
            }
        }



        paint.color = Color.BLUE
        canvas.drawCircle(ballX, ballY, radius, paint)


        if (isGoal) {
            paint.textSize = 80f

            val post = AsyncHttp("name", goaled())
            post.execute()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }

    private fun goaled(): Double {
        val elapsedTime = System.currentTimeMillis() - startTime
        val secTime = (elapsedTime / 1000).toDouble()
        return  secTime
    }

    private fun rad2Deg(rad: Float): Int {
        return Math.floor(Math.toDegrees(rad.toDouble())).toInt()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magField, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        ballX = (width / 2).toFloat()
        ballY = (height - radius).toFloat()
        startTime = System.currentTimeMillis()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maze_ball)

        maze.setStartHole(mole.getCurrentPosition())

        while (true) {
            var digPosition = mole.digHole(maze.getMazeField(), mazeSize)
            if (digPosition[0] == -1) break
            mole.setCurrentPosition(maze.diggedHole(digPosition, mole.getCurrentPosition()))
        }
        maze.setGoalHole(mole.getStartPosition())
        maze.generateMazeField()

        val holder = surfaceView.holder
        holder.addCallback(this)

        player = MediaPlayer.create(this, R.raw.alert)
        player.isLooping = true
    }

    override fun onResume() {
        super.onResume()
        player.start()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }
}

