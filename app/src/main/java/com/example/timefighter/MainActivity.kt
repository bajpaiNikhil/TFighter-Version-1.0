package com.example.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    internal var score = 0

    internal lateinit var tapeMeButton : Button
    internal  lateinit var gameScoreTextView : TextView
    internal  lateinit var timeLeftTextview : TextView


    internal  var gameStared = false

    internal lateinit var countDownTimer : CountDownTimer
    internal val initialCountDown : Long = 60000
    internal val countDownInterval : Long = 1000
    internal var timeLeftOnTimer : Long = 10000

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG , "onCreate Called . Score is  : $score")

        tapeMeButton = findViewById(R.id.tapMeButton)
        gameScoreTextView = findViewById(R.id.gameScoreTextview)
        timeLeftTextview = findViewById(R.id.timeLeftTextview)

        tapeMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)

            incrementScore()
        }

        if(savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        }else{
            resetGame()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu , menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.actionAbout){
            showInfo()
        }
        return true
    }

    private fun showInfo(){
        val dialogTitle = getString(R.string.aboutTitle , BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY,score)
        outState.putLong(TIME_LEFT_KEY , timeLeftOnTimer)
        countDownTimer.cancel()

        Log.d(TAG , "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG , "onDestriy Called")
    }

    private fun resetGame(){
        score = 0

        gameScoreTextView.text = getString(R.string.yourScore,score)

        val initialTimeLeft = initialCountDown / 1000
        timeLeftTextview.text = getString(R.string.timeLeft , initialTimeLeft)

        countDownTimer = object : CountDownTimer (initialCountDown , countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeLeftTextview.text = getString(R.string.timeLeft ,timeLeft)

            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStared = false
    }

    private fun restoreGame(){
        gameScoreTextView.text = getString(R.string.yourScore , score)

        val restoreTime = timeLeftOnTimer / 1000
        timeLeftTextview.text = getString(R.string.timeLeft , restoreTime)

        countDownTimer = object : CountDownTimer(timeLeftOnTimer , countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeLeftTextview.text = getString(R.string.timeLeft , timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        countDownTimer.start()
        gameStared = true
    }

    private fun incrementScore() {
        if(!gameStared){
            startGame()
        }

        score += 1
        val newScore = getString(R.string.yourScore , score)
        gameScoreTextView.text = newScore

        val blinkAnimation = AnimationUtils.loadAnimation(this , R.anim.blink)
        gameScoreTextView.startAnimation(blinkAnimation)

    }
    private fun startGame(){
        countDownTimer.start()
        gameStared = true
    }
    private  fun endGame(){
        Toast.makeText(this , getString(R.string.gameOverMessage , score) , Toast.LENGTH_LONG).show()
        resetGame()
    }

}