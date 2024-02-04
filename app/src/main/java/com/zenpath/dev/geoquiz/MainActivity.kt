package com.zenpath.dev.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.zenpath.dev.geoquiz.CheatActivity.Companion.EXTRA_ANSWER_SHOWN
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView

 //   private var answerList: MutableList<Boolean?> = MutableList(questionBank.size){null}

    private var isResult = false;

    companion object {
        private const val TAG = "MainActivity_"
        private const val KEY_INDEX = "index"
        private const val MAP_ANSWERS = "list_answers"
        private const val IS_RESULT = "is_result"

        private const val REQUEST_CODE_CHEAT = 0
    }

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this)[QuizViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
        val mapAns = savedInstanceState?.getSerializable(MAP_ANSWERS) as? Map<Int, Boolean>
        mapAns.let {
            if (mapAns != null) {
                quizViewModel.updateAnswersHistory(mapAns)
            }
        }

        val provider: ViewModelProvider = ViewModelProviders.of(this)
        val quizViewModel = provider[QuizViewModel::class.java]
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)

        trueButton.setOnClickListener {
            if(!isResult) {
                checkAnswer(true)
            }
        }

        falseButton.setOnClickListener {
            if(!isResult) {
                checkAnswer(false)
            }
        }

        nextButton.setOnClickListener {
            if (!quizViewModel.isFinal()) {
                quizViewModel.moveToNext()
                updateQuestion()
            }
        }

        prevButton.setOnClickListener {
            if(quizViewModel.currentIndex > 0) {
                quizViewModel.backToNext()
                updateQuestion()
            }
        }

        cheatButton.setOnClickListener {view ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue, quizViewModel.getCheatList.size)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options = ActivityOptions
                    .makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        updateQuestion()
        isResult = savedInstanceState?.getBoolean(IS_RESULT, false) ?: false
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putBoolean(IS_RESULT, isResult)

        val answers = quizViewModel.getAnswerList
        val map = mutableMapOf<Int, Boolean>()
        for (i in answers.indices){
            answers[i]?.let { map.put(i, it) }
        }
        savedInstanceState.putSerializable(MAP_ANSWERS, map as Serializable)
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            val isCheat = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            if(isCheat){
                quizViewModel.updateCheatHistory(quizViewModel.currentIndex)
            }
        }
    }

    private fun updateQuestion(){
        isResult = false
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean){
        quizViewModel.updateAnswerList(userAnswer)
        isResult = true
        val correctAnswer = quizViewModel.currentQuestionAnswer
        quizViewModel.refreshIsCheater()
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show()
        if(quizViewModel.isFinal()){
            showResult()
        }
    }

    private fun showResult(){
        Toast.makeText(this, "${quizViewModel.showResult()} %", Toast.LENGTH_SHORT)
            .show()
    }

}