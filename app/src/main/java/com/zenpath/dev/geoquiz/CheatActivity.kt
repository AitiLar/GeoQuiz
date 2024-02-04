package com.zenpath.dev.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button

    private var answerIsTrue = false

    companion object{
        const val EXTRA_ANSWER_CHEAT_ACTIVITY = "extra_answer_state"
        const val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown"
        private const val EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true"
        private const val EXTRA_CHEATER_COUNT = "com.bignerdranch.android.geoquiz.cheater_count"

        fun newIntent(packageContext: Context, answerIsTrue: Boolean, cheatingCount: Int): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
                putExtra(EXTRA_CHEATER_COUNT, cheatingCount)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        savedInstanceState?.let {
            answerIsTrue = it.getBoolean(EXTRA_ANSWER_CHEAT_ACTIVITY)
            updateStateShowAnswer()
        }
        showAnswerButton.setOnClickListener {
            updateStateShowAnswer()
            setAnswerShownResult(true)
        }
        val cheatingCount: Int = intent.getIntExtra(EXTRA_CHEATER_COUNT, 0)
        updateCheatingCount(cheatingCount)
    }

    fun updateCheatingCount(count: Int){
        val txtSdkVersion: TextView = findViewById(R.id.count_cheating);
        txtSdkVersion.text = count.toString()
        if(count > 2){
            showAnswerButton.isEnabled = false;
        }
    }

    fun updateStateShowAnswer(){
        val answerText = when {
            answerIsTrue -> R.string.true_button
            else -> R.string.false_button
        }
        answerTextView.setText(answerText)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_ANSWER_CHEAT_ACTIVITY, answerIsTrue)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }
}